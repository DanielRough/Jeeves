package com.jeeves.vpl.canvas.triggers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ParentPane;
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

import static com.jeeves.vpl.Constants.*;

@SuppressWarnings("rawtypes")

public abstract class Trigger extends ViewElement<FirebaseTrigger> {
	public static Trigger create(FirebaseTrigger exprmodel) {
		String classname = exprmodel.gettype();

		try {
			return (Trigger) Class.forName(classname).getConstructor(FirebaseTrigger.class).newInstance(exprmodel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	private ArrayList<Action> actions;
	private ActionReceiver childReceiver;

	private boolean loading = true;

	private Node root;

	protected ObservableMap<String, Object> params;

	public Trigger() {
		super(FirebaseTrigger.class);
	}


	public Trigger(FirebaseTrigger data) {
		super(data, FirebaseTrigger.class);
		this.model = data;
		int actionNumber = 0;
		if (model.gettriggerId() == null){
			System.out.println("NEEENAAWWWWNEEEEENAAAAAW");
			if(SHOULD_UPDATE_TRIGGERS)
			model.settriggerId(getSaltString());
		}
			if (actions != null)
			for(Action a : actions){
				System.out.println("Adding an action to " + childReceiver);
				childReceiver.addChildAtIndex(a, actionNumber++);
			}
		loading = false; // To check whether we change the salt string on the
		// first action initiation

	}

	@Override
	public void addListeners() {
		super.addListeners();
		params = FXCollections.observableHashMap();
		params.addListener(new MapChangeListener<String, Object>() {

			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) {
				System.out.println("5" + getInstance().toString());
				if(SHOULD_UPDATE_TRIGGERS)
					model.settriggerId(getSaltString()); // Again, update, must
				// reset
				if (change.wasAdded()) {
					model.getparams().put(change.getKey(), change.getValueAdded());
				} else {
					model.getparams().remove(change.getKey());
				}
			}

		});
		//If any new action is added
		childReceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
			if (loading == false){
				if(SHOULD_UPDATE_TRIGGERS)
					model.settriggerId(getSaltString()); // Need to update ID if
				System.out.println("6" + getInstance().toString());
		//		System.out.println("1" + getInstance().toString());

			}
				// actions change
			ArrayList<Action> newActions = new ArrayList<Action>();
			if (model.getactions() == null)
				model.setactions(new ArrayList<FirebaseAction>());
			model.getactions().clear();
			childReceiver.getChildElements().forEach(element -> {
				Action myaction = (Action)element;
				newActions.add(myaction);
				model.getactions().add((FirebaseAction) element.getModel());
				myaction.getparams().addListener(new MapChangeListener<String, Object>() {
					@Override
					public void onChanged(
							javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) {
						System.out.println("1" + getInstance().toString());
						if(SHOULD_UPDATE_TRIGGERS)
							model.settriggerId(getSaltString()); // Again, update,
						// must reset
					}

				});
				//Also listen on this action's expressions changing (if it has any)
				myaction.getVars().addListener(new ListChangeListener<FirebaseExpression>(){

					@Override
					public void onChanged(
							javafx.collections.ListChangeListener.Change<? extends FirebaseExpression> c) {
						System.out.println("2" + getInstance().toString());
						if(SHOULD_UPDATE_TRIGGERS)
							model.settriggerId(getSaltString()); // Again, update,
					}

				});
			});
		});
		//IF any old action is changed
		actions.forEach(myaction -> {
			myaction.getparams().addListener(new MapChangeListener<String, Object>() {
				@Override
				public void onChanged(
						javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) {
					System.out.println("3" + getInstance().toString());
					if(SHOULD_UPDATE_TRIGGERS)
						model.settriggerId(getSaltString()); // Again, update,
					// must reset
				}

			});
			//Also listen on this action's expressions changing (if it has any)
			myaction.getVars().addListener(new ListChangeListener<FirebaseExpression>(){

				@Override
				public void onChanged(
						javafx.collections.ListChangeListener.Change<? extends FirebaseExpression> c) {
					System.out.println("4" + getInstance().toString());
					if(SHOULD_UPDATE_TRIGGERS)
						model.settriggerId(getSaltString()); // Again, update,
				}

			});
		});


	}

	@Override
	public void fxmlInit() {
		this.type = ElementType.TRIGGER;
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		fxmlLoader.setLocation(getClass().getResource(getViewPath()));
		try {
			root = (Node) fxmlLoader.load();
			getChildren().add(root);
			childReceiver = new ActionReceiver();
			getChildren().add(childReceiver);

			double layouty = Math.max(((Pane) root).getPrefHeight(), ((Pane) root).getMinHeight());
			childReceiver.setLayoutY(layouty - 5);
			setPickOnBounds(false);
			((Pane) root).heightProperty().addListener(listen -> {
				double layout = ((Pane) root).getHeight();
				childReceiver.setLayoutY(layout - 5);
			});
			getChildren().forEach(child -> child.setPickOnBounds(false));

		} catch (IOException exception) {
			exception.printStackTrace();
			throw new RuntimeException(exception);
		}

	}

	public ArrayList<Action> getActions() {
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

	public abstract String getViewPath();

	@Override
	public void setParentPane(ParentPane parent) {
		super.setParentPane(parent);
		actions.forEach(action -> {
			action.setParentPane(parent);
		});

	}

	@Override
	protected void setData(FirebaseTrigger model) {
		super.setData(model);
		params = FXCollections.observableMap(model.getparams());
		double posX = model.getxPos();
		double posY = model.getyPos();
		Point2D position = new Point2D(posX, posY);
		List<FirebaseAction> onReceive = model.getactions();
		this.position = position;
		actions = new ArrayList<Action>();
		if (onReceive == null)
			return;
		for (FirebaseAction action : onReceive) {
			Action actionobj = Action.create(action);
			actions.add(actionobj);
		}
	}

}
