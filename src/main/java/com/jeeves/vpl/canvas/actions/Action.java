package com.jeeves.vpl.canvas.actions;

import static com.jeeves.vpl.Constants.actNames;

import java.io.IOException;

import com.jeeves.vpl.Constants.ElementType;
import com.jeeves.vpl.ViewElement;
import com.jeeves.vpl.firebase.FirebaseAction;
import com.jeeves.vpl.firebase.FirebaseExpression;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

/**
 * Actions that are performed on firing Triggers
 * @author Daniel
 */
public abstract class Action extends ViewElement<FirebaseAction> {
	public static Action create(FirebaseAction exprmodel) {
		String trigname = exprmodel.getname();
		String classname = "com.jeeves.vpl.canvas.actions." + actNames.get(trigname);		
		try {
			return (Action) Class.forName(classname).getConstructor(FirebaseAction.class).newInstance(exprmodel); 
		} catch (Exception e) {
			System.exit(1);
		}
		return null;
	}

	protected ObservableMap<String, Object> params;
	protected ObservableList<FirebaseExpression> vars;
	public Action() throws InstantiationException, IllegalAccessException {
		super(FirebaseAction.class.newInstance(),FirebaseAction.class);
	}

	public Action(FirebaseAction data) {
		super(data, FirebaseAction.class);


	}
	ListChangeListener<FirebaseExpression> varListener;
	@Override
	public void addListeners() {
		super.addListeners();
		params = FXCollections.observableHashMap();
		if (params != null) {
			params.addListener(
						(javafx.collections.MapChangeListener.Change<? extends String, ? extends Object> change) ->{

					if (change.wasAdded()) {
						model.getparams().put(change.getKey(), change.getValueAdded());
					} else {
						model.getparams().remove(change.getKey());
					}
				}

			);
		}
	}

	@Override
	public void fxmlInit() {
		this.type = ElementType.ACTION;
		this.model = new FirebaseAction();
		FXMLLoader fxmlLoader = new FXMLLoader();

		fxmlLoader.setController(this);
		fxmlLoader.setLocation(getClass().getResource("/" + getClass().getSimpleName() + ".fxml"));
		try {
			Node root = fxmlLoader.load();
			getChildren().add(root);
		} catch (IOException exception) {
			System.exit(1);
		}

	}

	@Override
	public Action getInstance() {
		return this;
	}

	public ObservableMap<String, Object> getparams() {
		return params;
	}
	public ObservableList<FirebaseExpression> getVars(){
		return vars;
	}

	@Override
	protected void setData(FirebaseAction data) {
		super.setData(data);
		vars = FXCollections.observableArrayList(model.getvars());
		vars.addListener((javafx.collections.ListChangeListener.Change<? extends FirebaseExpression> c) ->
				model.setvars(vars)
			
		);
	}
}
