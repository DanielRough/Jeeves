package com.jeeves.vpl.canvas.ifsloops;

import static com.jeeves.vpl.Constants.getSaltString;

import java.util.ArrayList;
import java.util.List;

import com.jeeves.vpl.ActionHolder;
import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.actions.Action;
import com.jeeves.vpl.canvas.expressions.Expression;
import com.jeeves.vpl.canvas.receivers.ActionReceiver;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseExpression;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * Class which represents a Control structure, such as a for loop, if-statement
 * or do-while loop. These contain actions, and are actions themselves
 * 
 * @author Daniel
 *
 */
@SuppressWarnings("rawtypes")
public abstract class Control extends Action implements ActionHolder {
	private ArrayList<Action> actions;
	@FXML
	private Pane pane;
	protected ActionReceiver childReceiver;
	@FXML
	protected HBox evalbox;
	protected ExpressionReceiver exprreceiver;

	public Control() {
		this(new FirebaseAction());
	}

	public Control(FirebaseAction data) {
		super(data);
	}

	@Override
	public void addListeners() {
		super.addListeners();
		childReceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
			ArrayList<Action> newActions = new ArrayList<Action>();
			if (model.getactions() == null)
				model.setactions(new ArrayList<FirebaseAction>());
			model.getactions().clear();
			params.put("update","updated");
			params.remove("update");
			childReceiver.getChildElements().forEach(element -> {
				Action myaction = (Action)element;
				newActions.add(myaction);
				model.getactions().add((FirebaseAction) element.getModel());
				myaction.getparams().addListener(new MapChangeListener<String, Object>() {
					@Override
					public void onChanged(
							javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) {
						//Merciless hack to update parent receiver
						params.put("update","updated");
						params.remove("update");
					}

				});
				//Also listen on this action's expressions changing (if it has any)
				myaction.getVars().addListener(new ListChangeListener<FirebaseExpression>(){

					@Override
					public void onChanged(
							javafx.collections.ListChangeListener.Change<? extends FirebaseExpression> c) {
						//Merciless hack to update parent receiver
						params.put("update","updated");
						params.remove("update");
					}
					
				});
			});
			
//			this.actions = newActions;
//			actions.forEach(myaction -> {
//				
//			});
		});
		actions.forEach(myaction -> {
			myaction.getparams().addListener(new MapChangeListener<String, Object>() {
				@Override
				public void onChanged(
						javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) {
					params.put("update","updated");
					params.remove("update");						// must reset
				}

			});
			//Also listen on this action's expressions changing (if it has any)
			myaction.getVars().addListener(new ListChangeListener<FirebaseExpression>(){

				@Override
				public void onChanged(
						javafx.collections.ListChangeListener.Change<? extends FirebaseExpression> c) {

					params.put("update","updated");
					params.remove("update");					}

			});
		});
		exprreceiver.getChildElements().addListener((ListChangeListener<ViewElement>) arg0 -> {
			if (!exprreceiver.getChildElements().isEmpty()) {
				ViewElement child = exprreceiver.getChildElements().get(0);
				Expression variable = ((Expression) child);
				
				//Here, whenever the parameters change, we remove and re-add it to the model.
				//This triggers a change in the getVars() of the Action, which in turn triggers a change in the trigger
				//Basically by adjusting the parameters of an expression in our action, we update the whole trigger config. Woohoo!
				variable.getparams().addListener(new MapChangeListener<String,Object>(){

					@Override
					public void onChanged(
							javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) {
						vars.clear();
						vars.add(0,variable.getModel());
						params.put("update","updated");
						params.remove("update");	
					}
					
				});
				vars.clear();
				vars.add(0, variable.getModel());				
				model.setcondition((FirebaseExpression) child.getModel());
			} else {
				vars.clear();

				model.setcondition(null);
			}
		});
		if(exprreceiver.getChildExpression() == null)return;
		ViewElement child = exprreceiver.getChildElements().get(0);
		Expression variable = ((Expression) child);
		
		//Here, whenever the parameters change, we remove and re-add it to the model.
		//This triggers a change in the getVars() of the Action, which in turn triggers a change in the trigger
		//Basically by adjusting the parameters of an expression in our action, we update the whole trigger config. Woohoo!
		variable.getparams().addListener(new MapChangeListener<String,Object>(){

			@Override
			public void onChanged(
					javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) {
				vars.clear();
				vars.add(0,variable.getModel());
				params.put("update","updated");
				params.remove("update");	
			}
			
		});
//		vars.clear();
//		vars.add(0, variable.getModel());				
//		model.setcondition((FirebaseExpression) child.getModel());
	}

	@Override
	public void fxmlInit() {
		super.fxmlInit();
		this.type = ElementType.CTRL_ACTION;
		childReceiver = new ActionReceiver();
		getChildren().add(childReceiver);
		childReceiver.setLayoutY(pane.getPrefHeight());

		pane.setPrefHeight(USE_COMPUTED_SIZE);
		childReceiver.getBrackets().getStyleClass().clear();
		childReceiver.getBrackets().getStyleClass().add("if_control");
		setPickOnBounds(false);
	}

	@Override
	public Control getInstance() {
		return this;
	}

	@Override
	public ActionReceiver getMyReceiver() {
		return childReceiver;
	}

	@Override
	public void setData(FirebaseAction model) {
		super.setData(model);
		actions = new ArrayList<Action>();
		if (model.getactions() != null) {
			List<FirebaseAction> onReceive = new ArrayList<FirebaseAction>(model.getactions()); 
			for (FirebaseAction action : onReceive) {
				Action myaction = Action.create(action);
				actions.add(myaction);

				childReceiver.addChild(myaction, 0, 0);
			}
		}
		FirebaseExpression condition = model.getcondition();
		if (condition == null)
			return;
		Expression expr = Expression.create(condition);
		exprreceiver.addChild(expr, 0, 0);
	}

	@Override
	public void setParentPane(ParentPane parent) {
		super.setParentPane(parent);
		actions.forEach(action -> {
			action.setParentPane(parent);
		});
		if (exprreceiver.getChildExpression() != null)
			exprreceiver.getChildExpression().setParentPane(parent);
	}

}