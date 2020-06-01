package org.saki.doridori.ui.gallery;

import java.util.ArrayList;
import java.util.List;

public class FilterCondition {
    public List<Integer> star = new ArrayList<>();
    public List<Integer> characterID = new ArrayList<>();
    public List<String> attribute = new ArrayList<>();

    FilterCondition(){
        int[] mstar = {1,2,3,4};
        for (int i:mstar) {
            this.star.add(i);
            }
        for(int a = 1; a < 31; a++){
            this.characterID.add(a);
        }
        this.attribute.add("powerful");
        this.attribute.add("cool");
        this.attribute.add("happy");
        this.attribute.add("pure");
    }

    public void setStar(List<Integer> star){
        this.star = star;
    }

    public void setBand(List<Integer> band){
        //1 Poppin'party
        //2 Aftergrow
        //3 Hello happy world
        //4 Pastel*Palette
        //5 Roselia
        //6 Monfonica
        this.characterID.clear();
        for(int a : band){
            for(int i = 1; i < 6; i++){
                this.characterID.add((a - 1)*5 + i);
            }
        }
    }

    public void setAttribute(List<String> attribute){this.attribute = attribute;}
}
