package gusev.dmitry.hiberExample2.helloWorld.annotated;

import javax.persistence.*;

/**
 * @author Gusev Dmitry (dgusev)
 * @version 1.0 (DATE: 02.12.11)
*/

@Entity
@Table(name = "ANNOTATEDMESSAGES")
public class AnnotatedMessage
 {
  @Id
  @GeneratedValue
  @Column(name = "MESSAGE_ID")
  private Long id;

  @Column(name = "MESSAGE_TEXT")
  private String text;

  @ManyToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "NEXT_MESSAGE_ID")
  private AnnotatedMessage nextMessage;

  private AnnotatedMessage() {}

  public AnnotatedMessage(String text) {this.text = text;}

  public Long getId() {return id;}
  private void setId(Long id) {this.id = id;}

  public String getText() {return text;}
  public void setText(String text) {this.text = text;}

  public AnnotatedMessage getNextMessage() {return nextMessage;}
  public void setNextMessage(AnnotatedMessage nextMessage) {this.nextMessage = nextMessage;}

 }