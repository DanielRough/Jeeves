package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_CATEGORY;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.jeeves.vpl.Constants;
import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener.Change;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;

public class CategoryExpression extends Expression { // NO_UCD (unused code)
	private static final String CATEGORY = "category";
	private static final String RESULT = "result";
	private ExpressionReceiver categoryReceiver;
	private ComboBox<String> cboCategories;

	public CategoryExpression(String name) {
		this(new FirebaseExpression(name));
	}

	
	@Override
	public void setParentPane(DragPane parent) {
		super.setParentPane(parent);
			if (categoryReceiver.getChildExpression() != null)
				categoryReceiver.getChildExpression().setParentPane(parent);
			categoryReceiver.getChildElements().addListener(
					(ListChangeListener<ViewElement>) listener -> {listener.next(); 
					if(listener.wasRemoved())return;
					categoryReceiver.getChildExpression().setParentPane(parent);
					});
					
	}
	
	@Override
	public void setData(FirebaseExpression model) {
		super.setData(model);
		updatePane();

		if (model.getparams().containsKey(CATEGORY)) {
			String category = model.getparams().get(CATEGORY).toString();
			setCategory(category);
		}
		if(model.getparams().containsKey(RESULT))
			cboCategories.setValue(model.getparams().get(RESULT).toString());
	}

	protected void setCategory(String category) {
		Constants.getOpenProject().getObservableVariables().forEach(variable -> {
			if(variable.getname().equals(category)) {
				categoryReceiver.addChild(UserVariable.create(variable),0,0);
				return;
			}});
		if (category != null && !category.equals("")) {
			//A thing to stop the category expressions inexplicably emptying themselves whenever a new attribute is added
			ListChangeListener<FirebaseVariable> varlistener= c ->
				Constants.getOpenProject().getObservableVariables().forEach(variable -> {
				if(variable.getname().equals(category)) {
					categoryReceiver.addChild(UserVariable.create(variable),0,0);
				}
					
				});			
			
			Constants.getOpenProject().registerVarListener(varlistener);
				
		}
	}

	
	public CategoryExpression(FirebaseExpression data) {
		super(data);
		
		categoryReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener -> {listener.next(); 
				if(categoryReceiver.getChildModel() != null) {
					params.put(CATEGORY, categoryReceiver.getChildModel().getname());
				}
				Constants.getCategoryOpts().addListener((Change<? extends String, ? extends String[]> arg0)->{
						String selected = cboCategories.getSelectionModel().getSelectedItem(); //Hopefully it has one
						cboCategories.getItems().clear();
						if(categoryReceiver.getChildModel() == null || !Constants.getCategoryOpts().containsKey(categoryReceiver.getChildModel().getname()))return;
						cboCategories.getItems().addAll(Constants.getCategoryOpts().get(categoryReceiver.getChildModel().getname()));
						cboCategories.setValue(selected);
						cboCategories.getSelectionModel().select(selected);
					
				});
				cboCategories.getItems().clear();
				if(categoryReceiver.getChildModel() == null || !Constants.getCategoryOpts().containsKey(categoryReceiver.getChildModel().getname()))return;
				Iterator<Map.Entry<String,String[]>> iter = Constants.getCategoryOpts().entrySet().iterator();
				while(iter.hasNext()){
					Entry<String,String[]> entry = iter.next();
				}
				cboCategories.getItems().addAll(Constants.getCategoryOpts().get(categoryReceiver.getChildModel().getname()));
				});
		cboCategories.getSelectionModel().selectedItemProperty().addListener((arg0,arg1,arg2) ->
				params.put(RESULT, cboCategories.getSelectionModel().getSelectedItem())
			);
	}

	@Override
	public void setup() {
		operand.setText("is equal to");
		categoryReceiver = new ExpressionReceiver(VAR_CATEGORY);

	}
	@Override
	public void updatePane() {
		super.updatePane();
		cboCategories = new ComboBox<>();
		cboCategories.setPrefHeight(20);
		cboCategories.setMinHeight(USE_PREF_SIZE);
		box.getChildren().addAll(categoryReceiver,operand,cboCategories);
		box.setPadding(new Insets(0, 10, 0, 10));

	}
}