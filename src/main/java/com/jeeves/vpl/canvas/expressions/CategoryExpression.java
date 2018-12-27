package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_CATEGORY;
import static com.jeeves.vpl.Constants.categoryOpts;

import java.util.List;

import com.jeeves.vpl.DragPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;

public class CategoryExpression extends Expression { // NO_UCD (unused code)
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

		if (model.getparams().containsKey("category")) {
			String category = model.getparams().get("category").toString();
			setCategory(category);
		}
		if(model.getparams().containsKey("result"))
			cboCategories.setValue(model.getparams().get("result").toString());
	}

	protected void setCategory(String category) {
		if (category != null && !category.equals("")) {
			//A thing to stop the category expressions inexplicably emptying themselves whenever a new attribute is added
			ListChangeListener<FirebaseVariable> varlistener= new ListChangeListener<FirebaseVariable>() {
				@Override
				public void onChanged(Change<? extends FirebaseVariable> c) {
					c.next();
					if(c.wasAdded()){
						List<FirebaseVariable> list = (List<FirebaseVariable>) c.getAddedSubList();
						if(list.get(0).getname().equals(category)) {
							categoryReceiver.addChild(UserVariable.create(list.get(0)),0,0);
							gui.unregisterVarListener(this);
						}
					}					
				}
				
			};
			gui.registerVarListener(varlistener);
				
		}
	}

	
	public CategoryExpression(FirebaseExpression data) {
		super(data);
		
		categoryReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener -> {listener.next(); 
				if(categoryReceiver.getChildModel() != null)
				params.put("category", categoryReceiver.getChildModel().getname());
				
				categoryOpts.addListener(new MapChangeListener<String,String[]>(){
					@Override
					public void onChanged(Change<? extends String, ? extends String[]> arg0) {
						String selected = cboCategories.getSelectionModel().getSelectedItem(); //Hopefully it has one
						cboCategories.getItems().clear();
						if(categoryReceiver.getChildModel() == null || !categoryOpts.containsKey(categoryReceiver.getChildModel().getname()))return;
						cboCategories.getItems().addAll(categoryOpts.get(categoryReceiver.getChildModel().getname()));
						cboCategories.setValue(selected);
						cboCategories.getSelectionModel().select(selected);
					}
					
				});
				cboCategories.getItems().clear();
				if(categoryReceiver.getChildModel() == null || !categoryOpts.containsKey(categoryReceiver.getChildModel().getname()))return;
				cboCategories.getItems().addAll(categoryOpts.get(categoryReceiver.getChildModel().getname()));
				});
		cboCategories.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				params.put("result", cboCategories.getSelectionModel().getSelectedItem());

			}
			
		});
	}

	@Override
	public void setup() {
		operand.setText("is equal to");
		categoryReceiver = new ExpressionReceiver(VAR_CATEGORY);

	}
	@Override
	public void updatePane() {
		super.updatePane();
		cboCategories = new ComboBox<String>();
		cboCategories.setPrefHeight(20);
		cboCategories.setMinHeight(USE_PREF_SIZE);
		box.getChildren().addAll(categoryReceiver,operand,cboCategories);
		box.setPadding(new Insets(0, 10, 0, 10));

	}
}