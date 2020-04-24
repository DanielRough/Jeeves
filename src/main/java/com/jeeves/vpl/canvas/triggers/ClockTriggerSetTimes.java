package com.jeeves.vpl.canvas.triggers;

import static com.jeeves.vpl.Constants.VAR_CLOCK;
import static com.jeeves.vpl.Constants.VAR_DATE;
import static com.jeeves.vpl.Constants.getSaltString;

import java.util.ArrayList;
import java.util.List;

import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.expressions.UserVariable;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.canvas.receivers.TimeReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseTrigger;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * This class represents a clock trigger that can be dragged around on the
 * canvas and have its fields edited
 *
 * @author Daniel
 */
public class ClockTriggerSetTimes extends ClockTrigger { // NO_UCD (use default)
	@FXML
	private Pane pane;
	@FXML
	private Pane paneEndDate;
	@FXML
	private Pane paneStartDate;
	@FXML
	protected Button btnAddTime;
	@FXML
	protected Button btnRemoveTime;
	@FXML
	protected VBox paneTimes;
	protected List<FirebaseExpression> times;
	protected List<UserVariable> vars;
	//
	public ClockTriggerSetTimes(String name) {
		this(new FirebaseTrigger(name));
	}

	public ClockTriggerSetTimes(FirebaseTrigger data) {
		super(data);
		paneStartDate.getChildren().add(dateReceiverFrom);
		paneEndDate.getChildren().add(dateReceiverTo);
	}


	@FXML
	public void handleAddTime() {
		TimeReceiver setTimeReceiver = new TimeReceiver(VAR_CLOCK);
		receivers.add(setTimeReceiver);
		pane.setPrefHeight(USE_COMPUTED_SIZE);
		paneTimes.getChildren().add(setTimeReceiver);
		int myindex = paneTimes.getChildren().size()-1;
		if(model.gettimes() == null)
			model.settimes(new ArrayList<FirebaseExpression>());
		model.gettimes().add(new FirebaseExpression());

		model.settriggerId(getSaltString());
		setTimeReceiver.getTextField().textProperty().addListener(listen -> {
			model.gettimes().get(myindex).setvalue(setTimeReceiver.getText());
			model.gettimes().get(myindex).setIsValue(true);
			model.settriggerId(getSaltString()); 

		});
		setTimeReceiver.setText("0");
		setTimeReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener -> {
					int index = receivers.indexOf(setTimeReceiver);
					listener.next();
					if(listener.wasAdded()){
						model.getvariables().add(setTimeReceiver.getChildModel().getname());
						times.set(index,setTimeReceiver.getChildModel());

					}
					else{
						FirebaseVariable blankVar = new FirebaseVariable();
						blankVar.setvalue(setTimeReceiver.getText());
						blankVar.setIsValue(true);
						ViewElement<?> removed = listener.getRemoved().get(0);
						FirebaseExpression removedModel = (FirebaseExpression)removed.getModel();
						model.getvariables().remove(removedModel.getname());
						times.set(index, blankVar);

					}
					model.settriggerId(getSaltString());
				});

	}

	@FXML
	public void handleRemoveTime() {
		ObservableList<Node> removedTimes = paneTimes.getChildren();
		if (removedTimes.isEmpty())
			return;
		TimeReceiver lastReceiver = (TimeReceiver) removedTimes.get(removedTimes.size() - 1);
		paneTimes.getChildren().remove(lastReceiver);
		int size = model.gettimes().size();
		model.gettimes().remove(size-1);//remove the last one
		receivers.remove(size-1); 
		model.settriggerId(getSaltString()); 

	}

	ArrayList<ExpressionReceiver> receivers;
	@Override
	public void setData(FirebaseTrigger model) {
		super.setData(model);
		receivers = new ArrayList<>();
		if(model.gettimes() == null)
			model.settimes(new ArrayList<FirebaseExpression>());
		vars = new ArrayList<>();

		this.times =  model.gettimes();
		if(times != null) {
			for(int i = 0; i < times.size(); i++){
				FirebaseExpression time = times.get(i);
				TimeReceiver newTimeReceiver = new TimeReceiver(VAR_CLOCK);
				receivers.add(newTimeReceiver);
				paneTimes.getChildren().add(newTimeReceiver);
				if(time.getisValue()){
					newTimeReceiver.setText(time.getvalue());
				}
				else{
					UserVariable timevar = UserVariable.create(time);
					newTimeReceiver.addChild(timevar, 0, 0);
					vars.add(timevar);
				}

				newTimeReceiver.getTextField().textProperty().addListener(listen -> {
					int index = receivers.indexOf(newTimeReceiver);
					times.get(index).setvalue(newTimeReceiver.getText());
					times.get(index).setIsValue(true);
					model.settriggerId(getSaltString()); // Again, update, must

				});
				newTimeReceiver.getChildElements().addListener(
						(ListChangeListener<ViewElement>) listener -> {
							int index = receivers.indexOf(newTimeReceiver);
							listener.next();
							if(listener.wasAdded()){
								model.getvariables().add(newTimeReceiver.getChildModel().getname());
								times.set(index,newTimeReceiver.getChildModel());

							}
							else{
								FirebaseVariable blankVar = new FirebaseVariable();
								blankVar.setvalue(newTimeReceiver.getText());
								blankVar.setIsValue(true);
								ViewElement<?> removed = listener.getRemoved().get(0);
								FirebaseExpression removedModel = (FirebaseExpression)removed.getModel();
								model.getvariables().remove(removedModel.getname());
								times.set(index, blankVar);


							}
							model.settriggerId(getSaltString());
						});
			}
		}
		pane.setPrefHeight(USE_COMPUTED_SIZE);


	}

	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);
		//This is the only reason we have the 'vars' arraylist. 
		vars.forEach(var->var.setParentPane(parent));
	}

}
