package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.annotations.Expose;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

public class DamageCardField extends Subject {

    final transient public Player player;
    @Expose
    private DamageCard card;
    @Expose
    private boolean visible;

    public DamageCardField(Player player) {
        this.player = player;
        this.card = null;
        this.visible = true;
    }

    public DamageCard getCard() {
        return card;
    }

    public void setCard(DamageCard card) {
        if (card != this.card) {
            this.card = card;
            notifyChange();
        }
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        if (visible != this.visible) {
            this.visible = visible;
            notifyChange();
        }
    }
}
