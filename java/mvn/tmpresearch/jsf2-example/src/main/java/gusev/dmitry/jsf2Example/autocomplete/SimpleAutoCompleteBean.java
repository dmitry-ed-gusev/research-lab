package gusev.dmitry.jsf2Example.autocomplete;

import javax.faces.bean.ManagedBean;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gusev Dmitry (Дмитрий)
 * @version 1.0 (DATE: 14.05.12)
*/

@ManagedBean
public class SimpleAutoCompleteBean {

 private String acValue;

 public String getAcValue() {
  return acValue;
 }

 public void setAcValue(String acValue) {
  this.acValue = acValue;
 }

 public List<String> getCompleteList(String query) {
  List<String> list = new ArrayList<String>();
  for(Player player : PlayerConverter.playerDB) {
   if (player.getName().contains(query)) {
    list.add(player.getName());
   }
  }
  return list;
 }

}
