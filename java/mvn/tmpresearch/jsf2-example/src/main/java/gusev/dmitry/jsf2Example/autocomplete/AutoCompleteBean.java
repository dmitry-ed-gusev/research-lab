package gusev.dmitry.jsf2Example.autocomplete;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.bean.ManagedBean;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
//@ApplicationScoped
public class AutoCompleteBean {

 private static final Log log = LogFactory.getLog(AutoCompleteBean.class);

 private Player selectedPlayer1;
 private Player selectedPlayer2;

 private List<Player> players;

 public AutoCompleteBean() {
  //players = PlayerConverter.playerDB;
 }

    public Player getSelectedPlayer1() {
        return selectedPlayer1;
    }

    public void setSelectedPlayer1(Player selectedPlayer1) {
        this.selectedPlayer1 = selectedPlayer1;
    }

    public Player getSelectedPlayer2() {
        return selectedPlayer2;
    }

    public void setSelectedPlayer2(Player selectedPlayer2) {
        this.selectedPlayer2 = selectedPlayer2;
    }

 /*
 public List<Player> completePlayer(String query) {
  List<Player> suggestions = new ArrayList<Player>();
  
  for(Player p : players) {
   if(p.getName().startsWith(query))
    suggestions.add(p);
  }
  
  return suggestions;
 }
 */

 public List<Player> getPlayersForComplete(String query) {

   log.debug("Getting list of players...");
   players = PlayerConverter.playerDB;

   List<Player> suggestions = new ArrayList<Player>();
   for(Player p : players) {
    if(/*p.getName().startsWith(query)*/ p.getName().contains(query))
     //p.setName("&lt;u&qt;" + p.getName() + "&lt;/u&qt;");
     suggestions.add(p);
   }

   return suggestions;
  }

}
