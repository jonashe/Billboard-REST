import org.json.JSONArray;
import org.json.JSONObject;

public class BillBoardJSONAdapter extends BillBoard implements BillBoardAdapterIf {
	BillBoardJSONAdapter(String ctxt) {
		super(ctxt);
	}
	
	@Override
	public String readEntries(String caller_ip) {
		JSONArray entries = new JSONArray();
		for(BillBoardEntry e: billboard){
			JSONObject entry = new JSONObject();
			entry.put("id", e.id);
			entry.put("disable_edits", !e.belongsToCaller(caller_ip));
			entry.put("text", e.text);
			
			entries.put(entry);
		}
		
		return entries.toString();
	}
	
	@Override
	public String readEntry(int idx, String caller_ip) {
		JSONObject entry = new JSONObject();
		BillBoardEntry bbe = getEntry(idx);
		if (bbe == null) {
			System.err.println("BillBoardServer - readEntry: Objekt null; ggf. ist Idx falsch");
			return null;
		}
		
		entry.put("id", bbe.id);
		entry.put("disable_edits", !bbe.belongsToCaller(caller_ip));
		entry.put("text", bbe.text);
		
		return entry.toString();
	}
}
