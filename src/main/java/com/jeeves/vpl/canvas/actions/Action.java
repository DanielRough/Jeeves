package com.jeeves.vpl.canvas.actions;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.firebase.FirebaseAction;

/**
 * Actions that are performed on firing Triggers
 * @author Daniel
 *
 */
public abstract class Action extends ViewElement<FirebaseAction> {
	public ObservableMap<String,Object> params = FXCollections.observableHashMap();
	public abstract String getViewPath();

	public Action(){
		super(FirebaseAction.class);
	}
	protected Action(FirebaseAction data) {
		super(data,FirebaseAction.class);
		
	}
	
	public static Action create(FirebaseAction exprmodel){
		String classname = exprmodel.gettype();
		try {
			return (Action)Class.forName(classname).getConstructor(FirebaseAction.class).newInstance(exprmodel); //It's a plain Action
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	


	public void fxmlInit(){
		this.type = ElementType.ACTION;
	//	setInitHeight(30);
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setController(this);
		fxmlLoader.setLocation(getClass().getResource(getViewPath()));
		try {
			Node root = (Node) fxmlLoader.load();
			getChildren().add(root);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
	@Override
	public Action getInstance() {
		return this;
	}

//	public void setData(FirebaseAction data){
//		super.setData(data);
//	}

	protected void addListeners(){
		super.addListeners();
		if(params != null)
			params.addListener(new MapChangeListener<String, Object>(){

				@Override
				public void onChanged(
						javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) {
					if(change.wasAdded()){
						model.getparams().put(change.getKey(), change.getValueAdded());
					}
					else{
						model.getparams().remove(change.getKey());
					}
					System.out.println("CHANGED PARAMS");
				}
				
			});

	}
}
