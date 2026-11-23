# SIMPLE

Streamlined inventory management plus loan equipment

## Build and run instructions

Requires JDK 21 or newer (`java -version` should show 21 or higher)

### Linux/macOS/WSL

Build and run:

```bash
./mvnw
```

With no arguments, the wrapper compiles and runs the app.

Test:

```bash
./mvnw test
```

### Windows

Build and run:

```powershell
.\mvnw.cmd
```

Test:

```powershell
.\mvnw.cmd test
```

## Troubleshooting

Scanning does nothing: browser extensions with keyboard shortcuts (e.g. Vimium) can stop the
scanner's keystrokes from reaching the interface. Disable them for the app's site, or test in an incognito window.

a
