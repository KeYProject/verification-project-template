# A template for larger Java verification projects with KeY Theorem Prover

This repository is a template for serious verification attempts of
Java source code with the KeY Theorem Prover. You should use it when
you are planning a larger case study.


Currently this template bundles KeY 2.10.0 and ci-tool 1.4.0. Both are
licensed under GPL.


## Features and Best Practices

* Store the version of KeY along with your proof and source files.
  Proof reloading is very sensitive accross of KeY versions and
  artifact changes. KeY 2.10.0 is checked into this bundle!
  
* You can change the bundled KeY version by dropping in a new UberJar,
  that are provided on the [download
  page](https://key-project.org/download). Please also change the path
  in the `Makefile` if necessary.
  
* Use an own KeY-file (`project.key`). This gives you flexibility to
  add own taclets or options.
  
* Organise your files and keep things separated. If you investigate
  multiple separated Java sources, you should store them in separated
  directories. KeY always loads complete source folders and never
  single Java files. Separation helps to keep (re-)loading times small.
  
* This template provides a Github workflow for continous proofability
  checking. For more options, consider the documentation of the
  [`ci-tool`](https://formal.iti.kit.edu/weigl/ci-tool/)
  

## File Overview 

1. Folder `Proofs` is to store your `*.proof` or `.proof.gz` files.
2. Use folder `src` to store the verification subject.
3. Use `project.key` to start the verification 
4. The Makefile allows you to run the KeY GUI and the ci-tool.


## Getting Started

To check the provability and compilability with:

```
$ make check
``` 

Start the KeY Gui using your `project.key` with

```
$ make run 
``` 
