# CLI as a homework for Software Design discipline

## Запуск программы

`bash$ ./gradlew`


## Описание реализации

### Алгоритм

![main_algorithm.png](readme_images/main_algorithm.png)

### Алгоритм работы BushScanner

![bush_scanner_algorithm.png](readme_images/bush_scanner_algorithm.png)

## Структура классов

```
ru.ifmo.jb.hw.sdcli
│   Main - entry point
│   BushScanner - input form console and parsing
│   Token – alias for String
│   
└── programs
    │   Program - abstract class that can execute() with input and output streams for piping
    │   'Some'Program - alternative program to shell program
    │   OuterProgram - executes actual program from bash
    │   NoneProgram - useful stub
```

### Program и его наследники

![program_inheritance.png](readme_images/program_inheritance.png)

### Что есть Token?

![token.png](readme_images/token.png)