package com.jeeves.vpl.canvas.actions;

import static com.jeeves.vpl.Constants.VAR_NUMERIC;
import static com.jeeves.vpl.Constants.numberHandler;

import java.util.Map;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.TextUtils;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

/**
 * Action to remain in the execution of a trigger temporarily
 * 
 * @author Daniel
 *
 */
public class WaitingAction extends Action { // NO_UCD (unused code)
	private static final String GRAN = "granularity";
	@FXML
	private ComboBox<String> cboWaitGranularity;
	@FXML
	private HBox hbox;
	@FXML
	private ExpressionReceiver numberReceiver;

	ChangeListener<String> selectionListener;

	public WaitingAction(String name) {
		this(new FirebaseAction(name));
	}

	public WaitingAction(FirebaseAction data) {
		super(data);
		cboWaitGranularity.getItems().addAll("seconds", "minutes", "hours");
	}

	@Override
	public void addListeners() {
		super.addListeners();
		numberReceiver = new ExpressionReceiver(VAR_NUMERIC);
		hbox.getChildren().add(1,numberReceiver);
		selectionListener = (arg0,arg1,arg2)->{
				if (arg2 != null) // aaaaaaaargh

					params.put(GRAN, arg2);

		};
		cboWaitGranularity.valueProperty().addListener(selectionListener);
	    numberReceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
			if (!numberReceiver.getChildElements().isEmpty()) {
				ViewElement<?> child = numberReceiver.getChildExpression();
				params.put("time", child.getModel());
			} else {
				params.put("time", "");
			}
		});
	    numberReceiver.getTextField().textProperty().addListener(listener->
	    	params.put("time", numberReceiver.getTextField().getText())
	    );
	
	}
	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);
		if(numberReceiver.getChildExpression() != null)
			numberReceiver.getChildExpression().setParentPane(parent);

	}

	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		Map<String, Object> params = model.getparams();
		if (params.isEmpty())
			return;

		if (params.containsKey(GRAN))
			cboWaitGranularity.setValue(params.get(GRAN).toString());
		if (params.containsKey("time"))
			if(params.get("time") instanceof Map) {
				setRecipient((Map<String,Object>)params.get("time"));
			}
			else {
				numberReceiver.getTextField().setText(params.get("time").toString());
			}
		}
	
	public void setRecipient(Map<String,Object> rec) {
		if(rec.isEmpty())
			return;
		String name = rec.get("name").toString();
		for(FirebaseVariable var : Constants.getOpenProject().getvariables()) {
			if(var.getname().equals(name)){
				numberReceiver.addChild(UserVariable.create(var), 0,0);
				setParentPane(parentPane);
				return;
			}
		}
		Constants.getOpenProject().registerVarListener(listener->{
			listener.next();
			if(listener.wasAdded()){
				for(FirebaseVariable var : listener.getAddedSubList()){
					if(var.getname().equals(name)){
						numberReceiver.addChild(UserVariable.create(var), 0,0);
						setParentPane(parentPane);
					}
					}
			}
		});
	}
	


}
