package com.jeeves.vpl.canvas.ifsloops;

import java.io.Serializable;
import java.util.ArrayList;

import com.jeeves.vpl.firebase.FirebaseAction;

/**
 * Created by Daniel on 08/06/15.
 */
public interface IControl extends Serializable { // NO_UCD (unused code)

	public ArrayList<FirebaseAction> getControlActions();
}
