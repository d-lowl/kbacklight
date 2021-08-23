# kbacklight

## Build and install

```
./gradlew build
cp build/bin/native/releaseExecutable/kbacklight.kexe ~/bin/kbacklight
```

Adjust paths as necessary

## Usage

### List backlights
```
kbacklight list
```

### Get backlight brightness
```
kbacklight get 
```

```
Usage: kBacklight get options_list
Arguments: 
    name -> Backlight name (optional) { String }
Options: 
    --current, -c [false] 
    --max, -m [false] 
    --percent, -p [false] 
    --help, -h -> Usage info 
```

### Set backlight brightness
```
kbacklight set <device> <value>
```

```
Usage: kBacklight set options_list
Arguments: 
    name -> Backlight name { String }
    value { Int }
Options: 
    --percent, -p [false] 
    --relative, -r [false] 
    --help, -h -> Usage info 
```

### Brighness dmenu
```
kbacklight dmenu # shows device list first
kbacklight dmenu --name <device> # prompts for command only
```

Command regex: `[+-]?\d+%?`
* If starts with + or - -- add the value to the current
* If ends with % -- use percentage