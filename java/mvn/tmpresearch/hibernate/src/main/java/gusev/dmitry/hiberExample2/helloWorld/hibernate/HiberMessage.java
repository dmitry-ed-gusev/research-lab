package gusev.dmitry.hiberExample2.helloWorld.hibernate;

public class HiberMessage
 {
 
  private Long id;
  private String text;
  private HiberMessage nextMessage;
  
  HiberMessage() {}
  
  public HiberMessage(String text) {this.text = text;}
  
  public Long getId() {return id;}
  private void setId(Long id) {this.id = id;}
  
  public String getText() {return text;}
  public void setText(String text) {this.text = text;}
  
  public HiberMessage getNextMessage() {return nextMessage;}
  public void setNextMessage(HiberMessage nextMessage) {this.nextMessage = nextMessage;}
  
 }