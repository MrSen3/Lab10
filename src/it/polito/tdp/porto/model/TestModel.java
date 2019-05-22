package it.polito.tdp.porto.model;

public class TestModel {

	public static void main(String[] args) {
		
		Model model = new Model();
		System.out.println("TODO: write a Model class and test it!");
		model.creaGrafo();
		
		Author partenza=model.getAutoriIdMap().get(719);
		Author arrivo=model.getAutoriIdMap().get(2185);
		model.trovaSequenza(partenza, arrivo);
		
	}

}
