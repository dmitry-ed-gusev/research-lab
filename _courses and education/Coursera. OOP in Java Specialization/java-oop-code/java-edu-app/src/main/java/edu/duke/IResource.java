package edu.duke;

public interface IResource {

    // return resource as iterable of words
    Iterable<String> words();

    // return resource as iterable of lines
    Iterable<String> lines();

}
