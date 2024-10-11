package ru.dmitriigusev.kata.lists;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** String single-linked list node. */

@Getter
@Setter
@AllArgsConstructor
public class StringListNode {

    private String         strValue;
    private StringListNode nextNode;

}
