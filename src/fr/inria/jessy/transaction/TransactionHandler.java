package fr.inria.jessy.transaction;

import java.util.UUID;

public class TransactionHandler {

	UUID id;
	
	public TransactionHandler(){
		this.id=UUID.randomUUID();
	}

	protected UUID getId() {
		return id;
	}
	
	
}