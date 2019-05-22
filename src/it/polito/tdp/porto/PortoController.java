package it.polito.tdp.porto;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.porto.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class PortoController {
	
	private Model model;
	
	private Author autoreScelto1;
	private Author autoreScelto2;
    
	@FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Author> boxPrimo;
    //Tutti gli autori
    @FXML
    private ComboBox<Author> boxSecondo;
    //Tutti gli autori che NON sono co-autori del primo

    @FXML
    private TextArea txtResult;



    @FXML
    void handleCoautori(ActionEvent event) {
    	txtResult.clear();
    	
    	autoreScelto1=boxPrimo.getValue();
    	if(autoreScelto1==null) {
    		txtResult.setText("Scegliere un autore dal primo menu a tendina\n");
    		return;
    	}
    	
    	List<Author> coautori = model.trovaCoAutori(autoreScelto1.getId());
    	Collections.sort(coautori);
    	txtResult.setText("I coautori dell' autore selezionato sono "+coautori.size()+":\n");
    	for(Author c: coautori) {
    		txtResult.appendText("- "+c+"\n");
    	}
    	
    	boxSecondo.getItems().addAll(model.trovaNonCoAutori(coautori, autoreScelto1));
    	boxSecondo.setDisable(false);
		
    }

    @FXML
    void handleSequenza(ActionEvent event) {
    	Author autoreScelto2 = boxSecondo.getValue();
    	if(autoreScelto2==null) {
    		txtResult.setText("Scegliere un autore dal secondo menu a tendina\n");
    		return;
    	}
    	
    	txtResult.appendText("La sequenza di pubblicazioni che va da "+autoreScelto1.toString()+" a "+autoreScelto2.toString()+":\n");
    	List<Paper> sequenzaPubblicazioni = new ArrayList<>(model.trovaSequenza(autoreScelto1, autoreScelto2));
    
    	for(Paper p: sequenzaPubblicazioni) {
    		txtResult.appendText("-"+p.toString()+"\n");
    	}
    
    }

    @FXML
    void initialize() {
        assert boxPrimo != null : "fx:id=\"boxPrimo\" was not injected: check your FXML file 'Porto.fxml'.";
        assert boxSecondo != null : "fx:id=\"boxSecondo\" was not injected: check your FXML file 'Porto.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Porto.fxml'.";

    }

	public void setModel(Model model) {
		this.model=model;
		boxPrimo.getItems().addAll(model.getAutori());
		boxSecondo.setDisable(true);
		this.model.creaGrafo();
	
		
	}
}
