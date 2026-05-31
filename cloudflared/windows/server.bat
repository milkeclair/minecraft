@echo off
setlocal

:main
  call :install_cloudflared
  call :login_to_cloudflare
  call :check_need_to_open_tunnel
  call :search_already_exist_tunnel

  set IS_TUNNEL_EXIST=%ERRORLEVEL%

  if %IS_TUNNEL_EXIST%==0 (
    call :ensure_tunnel_setting
  ) else (
    call :create_tunnel
    call :setting_tunnel
  )

  set IS_SUCCESSFULLY_WRITTEN=%ERRORLEVEL%
  if %IS_SUCCESSFULLY_WRITTEN%==0 (
    call :add_dns_record_to_cloudflare
    call :open_tunnel
  ) else (
    echo Failed to open tunnel
    pause
  )
exit

:: private

:: null->void
:install_cloudflared
  echo --------------------------------------------------
  echo Step1: Install Cloudflared
  echo --------------------------------------------------

  winget install --id Cloudflare.cloudflared

  echo.
  echo Successfully installed
  echo.
exit /b

:: null->void
:login_to_cloudflare
  echo --------------------------------------------------
  echo Step2: Login to Cloudflare
  echo --------------------------------------------------

  if exist "%USERPROFILE%\.cloudflared\cert.pem" (
    echo Existing certificate found, skipping login
  ) else (
    cloudflared tunnel login
  )

  echo.
  echo Successfully logged in
  echo.
exit /b

:: null->void
:check_need_to_open_tunnel
  echo --------------------------------------------------
  echo Step3: Search or create Tunnel
  echo --------------------------------------------------
  echo Existing tunnels:

  cloudflared tunnel list

  echo --------------------------------------------------
  echo.
exit /b

:: null->bool_int
:search_already_exist_tunnel
  :: null->void
  :check_tunnel_name
    set /p TUNNEL_NAME="Enter tunnel name: "
    :: Validation
    if "%TUNNEL_NAME%"=="" (
      echo.
      echo Tunnel name is required
      echo.
      goto check_tunnel_name
    )

  set IS_TUNNEL_EXIST=1

  :: Check if tunnel already exists
  for /f "tokens=1,2 delims= " %%a in (
    'cloudflared tunnel list ^| findstr /C:"%TUNNEL_NAME%"'
  ) do (
    if "%%b"=="%TUNNEL_NAME%" (
      call :hp_set_tunnel_id %%a
      call :hp_set_tunnel_exist 0
    )
  )

  if "%IS_TUNNEL_EXIST%"=="0" (
    echo.
    echo Tunnel already exists
    echo.
    exit /b 0
  )
exit /b 1

:: bool->void
:hp_set_tunnel_exist
  set IS_TUNNEL_EXIST=%1
exit /b

:: null->void
:ensure_tunnel_setting
  if "%TUNNEL_ID%"=="" (
    echo Failed to detect tunnel id
    exit /b 1
  )

  set CONFIG_PATH=%USERPROFILE%\.cloudflared\%TUNNEL_NAME%\config.yaml

  if not exist "%CONFIG_PATH%" (
    echo Existing tunnel has no local config
    call :setting_tunnel
    if errorlevel 1 exit /b 1
    exit /b 0
  )

  call :load_host_name_from_config
  if %ERRORLEVEL%==0 (
    echo Existing config found
    exit /b 0
  )

  echo Failed to load hostname from existing config
exit /b 1

:: null->bool_int
:load_host_name_from_config
  set HOST_NAME=
  for /f "tokens=2 delims=:" %%a in (
    'findstr /C:"hostname:" "%CONFIG_PATH%"'
  ) do (
    call :hp_set_host_name_from_config %%a
  )

  if "%HOST_NAME%"=="" (
    exit /b 1
  )
exit /b 0

:: str->void
:hp_set_host_name_from_config
  set HOST_NAME=%1
exit /b

:: null->void
:create_tunnel
  set TUNNEL_SEARCH_TEXT=Created tunnel %TUNNEL_NAME% with id
  :: Create tunnel and get tunnel id
  for /f "tokens=6 delims= " %%a in (
    'cloudflared tunnel create %TUNNEL_NAME% ^| findstr /C:"%TUNNEL_SEARCH_TEXT%"'
  ) do (
    call :hp_set_tunnel_id %%a
  )

  echo.
  echo Successfully created tunnel
exit /b

:: str->void
:hp_set_tunnel_id
  set TUNNEL_ID=%1
exit /b

:: null->void
:setting_tunnel
  :: null->void
  :check_hostname
    set /p HOST_NAME="Enter hostname (e.g. sub.example.com): "
    :: Validation
    if "%HOST_NAME%"=="" (
      echo.
      echo Hostname is required
      echo.
      goto check_hostname
    )

  :: null->void
  :check_protocol
    echo "If you want to access the minecraft server, you can use tcp."
    set /p PROTOCOL="Enter protocol (e.g. https): "
    :: Validation
    if "%PROTOCOL%"=="" (
      echo.
      echo Protocol is required
      echo.
      goto check_protocol
    )

  :: null->void
  :check_port
    set /p PORT="Enter port (e.g. 80): "
    :: Validation
    if "%PORT%"=="" (
      echo.
      echo Port is required
      echo.
      goto check_port
    )

  timeout /t 3 >nul

  :: Write setting to file
  set RETRY_COUNT=0
  set MAX_RETRIES=3

  mkdir %USERPROFILE%\.cloudflared\%TUNNEL_NAME%

  :: null->bool_int
  :retry_write_setting
    (
      echo tunnel: %TUNNEL_ID%
      echo credentials-file: %USERPROFILE%\.cloudflared\%TUNNEL_ID%.json
      echo ingress:
      echo   - hostname: %HOST_NAME%
      echo     service: %PROTOCOL%://localhost:%PORT%
      echo   - service: http_status:404
    ) > "%USERPROFILE%\.cloudflared\%TUNNEL_NAME%\config.yaml" || (
      set /a RETRY_COUNT+=1

      if %RETRY_COUNT% lss %MAX_RETRIES% (
        timeout /t 3 >nul
        goto retry_write_setting
      ) else (
        echo Failed to write setting to file;; stopping...
        exit /b 1
      )
    )

  echo.
  echo Successfully wrote setting to file
exit /b 0

:: null->void
:add_dns_record_to_cloudflare
  set IS_DNS_RECORD_EXIST=1
  set TEMP_OUTPUT=%TEMP%\dns_output.txt

  cloudflared tunnel route dns %TUNNEL_NAME% %HOST_NAME% > "%TEMP_OUTPUT%" 2>&1
  findstr /C:"host already exists" "%TEMP_OUTPUT%" > nul
  if %ERRORLEVEL%==0 (
    call :hp_set_dns_record_exist 0
  )
  del "%TEMP_OUTPUT%" 2>nul

  echo.
  if "%IS_DNS_RECORD_EXIST%"=="0" (
    echo DNS record already exists
  ) else (
    echo Successfully added DNS record
  )
  echo.
exit /b

: bool->void
:hp_set_dns_record_exist
  set IS_DNS_RECORD_EXIST=%1
exit /b

:: null->void
:open_tunnel
  echo --------------------------------------------------
  echo Step4: Open Tunnel
  echo --------------------------------------------------
  echo if you want to close the tunnel, press Ctrl+C
  echo.

  :: null->void
  :get_current_time
    set YYYY=%date:~0,4%
    set MM=%date:~5,2%
    set DD=%date:~8,2%

    set HH=%time:~0,2%
    set HH=%HH: =0%
    set MIN=%time:~3,2%

    set CURRENT_TIME=%YYYY%-%MM%-%DD%-%HH%-%MIN%

  set CONFIG_PATH=%USERPROFILE%\.cloudflared\%TUNNEL_NAME%\config.yaml
  set LOG_PATH=%USERPROFILE%\.cloudflared\%TUNNEL_NAME%\logs\%CURRENT_TIME%.log

  cloudflared tunnel --logfile "%LOG_PATH%" --config "%CONFIG_PATH%" run %TUNNEL_NAME%
exit /b

endlocal
