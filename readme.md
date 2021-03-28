# CLI as a homework for Software Design discipline

## Запуск программы

`bash$ ./gradlew`


## Описание реализации

### Алгоритм

<img src="readme_images/main_algorithm.png" width="70%">

### Алгоритм работы BushScanner

<img src="readme_images/bush_scanner_algorithm.png" width="40%">

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

<img src="readme_images/program_inheritance.png" width="40%">

### Что есть Token?

<img src="readme_images/token.png" width="50%">

## UML (ДЗ №4)

### Диаграмма случаев использования

<img src="readme_images/use-case-diagram.png" width="70%">

### Диаграмма компонентов

<img src="readme_images/component-diagram.png" width="70%">

### Диаграммы последовательностей

<img src="readme_images/sequence-diagram.png" width="60%">