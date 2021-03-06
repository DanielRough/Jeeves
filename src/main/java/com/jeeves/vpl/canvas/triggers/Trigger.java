package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.getSaltString;

import static com.jeeves.vpl.Constants.trigNames;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.Constants;
import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.actions.Action;
import com.jeeves.vpl.canvas.receivers.ActionReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseTrigger;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

@SuppressWarnings("rawtypes")

public abstract class Trigger extends ViewElement<FirebaseTrigger> {
	public static Trigger create(FirebaseTrigger exprmodel) {
		String trigname = exprmodel.getname();
		String classname = "com.jeeves.vpl.canvas.triggers." + trigNames.get(trigname);
		try {
			return (Trigger) Class.forName(classname).getConstructor(FirebaseTrigger.class).newInstance(exprmodel);
		} catch (Exception e) {
			System.exit(1);
		}
		return null;
	}


	private ArrayList<Action> actions;
	private ActionReceiver childReceiver;

	private boolean loading = true;


	protected ObservableMap<String, Object> params;

	public Trigger() throws InstantiationException, IllegalAccessException {
		super(FirebaseTrigger.class.newInstance(),FirebaseTrigger.class);
	}


	public Trigger(FirebaseTrigger data) {
		super(data, FirebaseTrigger.class);
		int actionNumber = 0;
		if (model.gettriggerId() == null && Constants.shouldUpdateTriggers()){
			model.settriggerId(getSaltString());
		}
			if (actions != null) {
			for(Action a : actions){
				childReceiver.addChildAtIndex(a, actionNumber++);
			}
			}
			addActionListeners();
		loading = false; // To check whether we change the salt string on the
		// first action initiation

	}

	private void addActionListeners() {
		//IF any old action is changed
		actions.forEach(myaction -> {
			myaction.getparams().addListener((MapChangeListener.Change<? extends String, ? extends Object> change) -> {
					if(Constants.shouldUpdateTriggers())
						model.settriggerId(getSaltString());
			});
			//Also listen on this action's expressions changing (if it has any)
			myaction.getVars().addListener((ListChangeListener.Change<? extends FirebaseExpression> c) -> {
					if(Constants.shouldUpdateTriggers())
						model.settriggerId(getSaltString()); // Again, update,

			});
		});
	}
	
	public void addParamsListener() {
		params.addListener((MapChangeListener.Change<? extends String, ? extends Object> change) ->{
				if(Constants.shouldUpdateTriggers()) {
					model.settriggerId(getSaltString());
				}
				if (change.wasAdded()) {
					model.getparams().put(change.getKey(), change.getValueAdded());
				} else {
					model.getparams().remove(change.getKey());
				}

		});
	}
	@Override
	public void addListeners() {
		super.addListeners();
		params = FXCollections.observableMap(model.getparams());
		addParamsListener();

		//If any new action is added
		childReceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
			if (!loading && Constants.shouldUpdateTriggers()){
					model.settriggerId(getSaltString()); // Need to update ID if
			}
				// actions change
			if (model.getactions() == null)
				model.setactions(new ArrayList<FirebaseAction>());
			model.getactions().clear();
			childReceiver.getChildElements().forEach(this::addChildListener);
		});



	}

	public void addChildListener(ViewElement element) {
		ArrayList<Action> newActions = new ArrayList<>();

		Action myaction = (Action)element;
		newActions.add(myaction);
		model.getactions().add((FirebaseAction) element.getModel());
		myaction.getparams().addListener((MapChangeListener.Change<? extends String, ? extends Object> change) ->{
				if(Constants.shouldUpdateTriggers())
					model.settriggerId(getSaltString()); // Again, update,
				// must reset

		});
		//Also listen on this action's expressions changing (if it has any)
		myaction.getVars().addListener((ListChangeListener.Change<? extends FirebaseExpression> c) -> {
				if(Constants.shouldUpdateTriggers())
					model.settriggerId(getSaltString()); // Again, update,
			

		});
	}
	@Override
	public void fxmlInit() {
		Node root;
		this.type = ElementType.TRIGGER;
		//Need to initialise a new model
		this.model = new FirebaseTrigger();
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		fxmlLoader.setLocation(getClass().getResource("/" + getClass().getSimpleName() + ".fxml"));
		try {
			root = fxmlLoader.load();
			getChildren().add(root);
			childReceiver = new ActionReceiver();
			getChildren().add(childReceiver);

			double layouty = Math.max(((Pane) root).getPrefHeight(), ((Pane) root).getMinHeight());
			childReceiver.setLayoutY(layouty);
			setPickOnBounds(false);
			((Pane) root).heightProperty().addListener(listen -> {
				double layout = ((Pane) root).getHeight();
				childReceiver.setLayoutY(layout);
			});
			getChildren().forEach(child -> child.setPickOnBounds(false));

		} catch (IOException exception) {
			System.exit(1);
		}

	}

	public List<Action> getActions() {
		return actions;
	}

	@Override
	public ViewElement<FirebaseTrigger> getInstance() {
		return this;
	}

	@Override
	public FirebaseTrigger getModel() {
		return model;
	}

	public ActionReceiver getMyReceiver() {
		return childReceiver;
	}


	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);
		actions.forEach(action ->
			action.setParentPane(parent)
		);

	}

	@Override
	protected void setData(FirebaseTrigger model) {
		super.setData(model);
		params = FXCollections.observableMap(model.getparams());
		addParamsListener();
		double posX = model.getxPos();
		double posY = model.getyPos();
		Point2D position = new Point2D(posX, posY);
		List<FirebaseAction> onReceive = model.getactions();
		this.position = position;
		actions = new ArrayList<>();
		if (onReceive == null)
			return;
		for (FirebaseAction action : onReceive) {
			Action actionobj = Action.create(action);
			actions.add(actionobj);
		}
	}

}
