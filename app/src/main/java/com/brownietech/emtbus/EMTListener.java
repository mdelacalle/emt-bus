package com.brownietech.emtbus;

import java.util.ArrayList;

public interface  EMTListener {
    public void onSuccess(ArrayList<Arrive> arrives) ;
   public void onError(String s);
}
