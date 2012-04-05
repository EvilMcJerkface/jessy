package fr.inria.jessy.benchmark.ycsb;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import com.yahoo.ycsb.DB;

import fr.inria.jessy.Jessy;
import fr.inria.jessy.LocalJessy;

public class JessyDBClient extends DB {
	
	
	/*Variables*/
	private Jessy jessy;
	private OutputStreamWriter log ;
	private OutputStreamWriter err ;
	private int oper;
	public JessyDBClient() {
		super();
		init();
		
	}
	@Override
	public void init() {
		try {
			jessy = LocalJessy.getInstance();
			jessy.addEntity(YCSBEntity.class);
			File f = new File("errors");
			f.delete();
			f = new File("log");
			f.delete();
			FileOutputStream fos = new FileOutputStream("errors");
			DataOutputStream dos = new DataOutputStream(fos);
			err = new OutputStreamWriter(dos);
			
			 fos = new FileOutputStream("log");
			 dos = new DataOutputStream(fos);
			log = new OutputStreamWriter(dos);
			oper = 0;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
	}
	@Override
	public int read(String table, String key, Set<String> fields,
			HashMap<String, String> result) {
		Operation op = new Operation(oper, key+table, OPState.UNKNOWN, OPType.READ);
		oper++;
		try {
			YCSBEntity en  = jessy.read(YCSBEntity.class, key+table);
			if (en == null)
			{	
				/*null fail : the read function of jessy returns null*/
				op.setState(OPState.NULLFAILED);
				err.write(op.toString());
				log.write(op.toString());
				return -1;
			}
		} catch (Exception e) {
			/*exception fail : we have an exception when trying to run */
			op.setState(OPState.EXCEPTIONFAILED);
			try {
				err.write(op.toString());
				log.write(op.toString());
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			
			return -1;
		}
		/*success*/
		op.setState(OPState.SUCCESS);
		try {
			
			log.write(op.toString());
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		return 0;
	}

	@Override
	public int scan(String table, String startkey, int recordcount,
			Set<String> fields, Vector<HashMap<String, String>> result) {
		/*Unsupported*/
		return 0;
	}

	@Override
	public int update(String table, String key, HashMap<String, String> values) {
		Operation op = new Operation(oper, key+table, OPState.UNKNOWN, OPType.UPDATE);
		oper++;
		try {
			YCSBEntity en  = jessy.read(YCSBEntity.class, key+table);
			if (en == null)
			{	
				/*null fail : the read function of jessy returns null*/
				op.setState(OPState.NULLFAILED);
				err.write(op.toString());
				log.write(op.toString());
				return -1;
			}
			en.setFields(values);
			jessy.write(en);
		} catch (Exception e) {
			/*exception fail : we have an exception when trying to run */
			op.setState(OPState.EXCEPTIONFAILED);
			try {
				err.write(op.toString());
				log.write(op.toString());
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			
			return -1;
		}
		/*success*/
		op.setState(OPState.SUCCESS);
		try {
			
			log.write(op.toString());
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		return 0;
	}

	@Override
	public int insert(String table, String key, HashMap<String, String> values) {
		Operation op = new Operation(oper, key+table, OPState.UNKNOWN, OPType.WRITE);
		oper++;
		try {
			YCSBEntity en  = new YCSBEntity(YCSBEntity.class.toString(),table+key,values);
			jessy.write(en);
		} catch (Exception e) {
			/*exception fail : we have an exception when trying to run */
			op.setState(OPState.EXCEPTIONFAILED);
			try {
				err.write(op.toString());
				log.write(op.toString());
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			
			return -1;
		}
		/*success*/
		op.setState(OPState.SUCCESS);
		try {
			
			log.write(op.toString());
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		return 0;
	}
	/*Delete : deletes an entry from the table*/
	@Override
	public int delete(String table, String key) {
		Operation op = new Operation(oper, key+table, OPState.UNKNOWN, OPType.UPDATE);
		oper++;
		try {
			YCSBEntity en  = jessy.read(YCSBEntity.class, key+table);
			if (en == null)
			{	
				/*null fail : the read function of jessy returns null*/
				op.setState(OPState.NULLFAILED);
				err.write(op.toString());
				log.write(op.toString());
				return -1;
			}
			en.removeAll();
			jessy.write(en);
		} catch (Exception e) {
			/*exception fail : we have an exception when trying to run */
			op.setState(OPState.EXCEPTIONFAILED);
			try {
				err.write(op.toString());
				log.write(op.toString());
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			
			return -1;
		}
		/*success*/
		op.setState(OPState.SUCCESS);
		try {
			
			log.write(op.toString());
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		return 0;
	}

}