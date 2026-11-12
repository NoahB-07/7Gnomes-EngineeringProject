package com.seven_gnomes;

interface character{
    /**
     * Definition of the characteristics needed for all characters
     */

    void move();        //might have to seperate out for more specific controlls for the player
    void interact();
    void decreaseHealth();
    void increaseHealth();
    void attack();

}

interface player extends character{
    /**
     * Definition of the character controlled by the player
     */

    void pickupItem();
    void swapItemLeft();    //controlling inventory of items
    void swapItemRight();


}

interface goose extends character{

    /**
     * Definition of the geese
     */

    void eat();
    void vanish();

}