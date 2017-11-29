package com.jeeves.vpl.canvas.expressions;

import static com.jeeves.vpl.Constants.VAR_BOOLEAN;
import static com.jeeves.vpl.Constants.VAR_CATEGORY;
import static com.jeeves.vpl.Constants.categoryOpts;
import static com.jeeves.vpl.Constants.styleTextCombo;

import java.util.List;

import com.jeeves.vpl.ParentPane;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.canvas.receivers.ExpressionReceiver;
import com.jeeves.vpl.firebase.FirebaseExpression;
import com.jeeves.vpl.firebase.FirebaseVariable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;

public class CategoryExpression extends Expression { // NO_UCD (unused code)
	public static final String DESC = "evaluates to true if the category attribute is currently the selected category";
	public static final String NAME = "Category";
	private ExpressionReceiver categoryReceiver;
	private ComboBox<String> cboCategories;

	public CategoryExpression() {
		this(new FirebaseExpression());
	}

	
	@Override
	public void setParentPane(ParentPane parent) {
		super.setParentPane(parent);
			if (categoryReceiver.getChildExpression() != null)
				categoryReceiver.getChildExpression().setParentPane(parent);
			categoryReceiver.getChildElements().addListener(
					(ListChangeListener<ViewElement>) listener -> {listener.next(); 
					if(listener.wasRemoved())return;
					//model.getparams().put("result", locReceiver.getChildModel().getname());
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
				gui.registerVarListener(listener->{
					listener.next();
					if(listener.wasAdded()){
						List<FirebaseVariable> list = (List<FirebaseVariable>) listener.getAddedSubList();
						if(list.get(0).getname().equals(category))
							categoryReceiver.addChild(UserVariable.create(list.get(0)),0,0);
					}
				});
				
		}
	}
	@Override
	public Node[] getWidgets() {
		return new Node[] {cboCategories};
	}
	
	public CategoryExpression(FirebaseExpression data) {
		super(data);
		
		categoryReceiver.getChildElements().addListener(
				(ListChangeListener<ViewElement>) listener -> {listener.next(); 
				//model.getparams().put("result", locReceiver.getChildModel().getname());
				if(categoryReceiver.getChildModel() != null)
				params.put("category", categoryReceiver.getChildModel().getname());
				
				//This fires when the options for any question that we've assigned to a particular category gets its options changed
				categoryOpts.addListener(new MapChangeListener<String,String[]>(){
					@Override
					public void onChanged(Change<? extends String, ? extends String[]> arg0) {
						cboCategories.getItems().clear();
//						if(categoryOpts == null)System.out.println("category opts is null");
	//					if(categoryReceiver == null)System.out.println("receiver is null");
		//				if(categoryReceiver.getChildModel() == null)System.out.println("Model is null");
						//Bleugh this is awful
						if(categoryReceiver.getChildModel() == null || !categoryOpts.containsKey(categoryReceiver.getChildModel().getname()))return;
						cboCategories.getItems().addAll(categoryOpts.get(categoryReceiver.getChildModel().getname()));
					}
					
				});
				cboCategories.getItems().clear();
				if(categoryReceiver.getChildModel() == null || !categoryOpts.containsKey(categoryReceiver.getChildModel().getname()))return;
				cboCategories.getItems().addAll(categoryOpts.get(categoryReceiver.getChildModel().getname()));
				});// timeReceiverFrom.getChildElements().get(0).getModel())));		
		cboCategories.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				params.put("result", cboCategories.getSelectionModel().getSelectedItem());

			}
			
		});
	}

	@Override
	public void setup() {
		name = NAME;
		description = DESC;
		this.varType = VAR_BOOLEAN;
		operand.setText("is equal to");
		categoryReceiver = new ExpressionReceiver(VAR_CATEGORY);
//		receivers.add(new ExpressionReceiver(VAR_CATEGORY));
//		receivers.add(new ExpressionReceiver(VAR_NUMERIC));

	}
	@Override
	public void updatePane() {
		super.updatePane();
		cboCategories = new ComboBox<String>();
		styleTextCombo(cboCategories);
		cboCategories.setPrefHeight(20);
		cboCategories.setMinHeight(USE_PREF_SIZE);
		box.getChildren().addAll(categoryReceiver,operand,cboCategories);
		box.setPadding(new Insets(0, 10, 0, 10));

	}
}