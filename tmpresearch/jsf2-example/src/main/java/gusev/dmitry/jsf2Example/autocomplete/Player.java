package gusev.dmitry.jsf2Example.autocomplete;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 14.05.12)
*/

public class Player {

 private String name;
 private int number;

 public Player() {
 }

 public Player(String name, int number) {
  this.name = name;
  this.number = number;
 }

 public String getName() {
  return name;
 }

 public void setName(String name) {
  this.name = name;
 }

 public int getNumber() {
  return number;
 }

 public void setNumber(int number) {
  this.number = number;
 }

}