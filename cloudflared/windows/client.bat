@echo off

:main
  call :install_cloudflared
  call :dig_tunnel_to_server
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
:dig_tunnel_to_server
  echo --------------------------------------------------
  echo Step2: Dig tunnel to server
  echo --------------------------------------------------

  :: null->void
  :check_hostname
    set /p HOST_NAME="Enter host name (e.g. example.com): "
    :: Validation
    if "%HOST_NAME%"=="" (
      echo.
      echo Host name is required
      echo.
      goto check_hostname
    )

  :: null->void
  :check_protocol
    echo "If you want to access the minecraft server, you can use tcp."
    set /p PROTOCOL="Enter protocol (e.g. http, https, tcp): "
    :: Validation
    if "%PROTOCOL%"=="" (
      echo.
      echo Protocol is required
      echo.
      goto check_protocol
    )

  :: null->void
  :check_port
    set /p PORT="Enter local port (e.g. 80): "
    :: Validation
    if "%PORT%"=="" (
      echo.
      echo Local port is required
      echo.
      goto check_port
    )

  echo if you need to stop the tunnel, press Ctrl+C
  cloudflared access %PROTOCOL% --hostname %HOST_NAME% --url 127.0.0.1:%PORT%
exit /b
