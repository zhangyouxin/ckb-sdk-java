package indexer;

import com.google.gson.Gson;
import indexer.model.Script;
import indexer.model.ScriptType;
import indexer.model.SearchKeyBuilder;
import indexer.model.resp.CellsResponse;
import java.io.IOException;
import java.math.BigInteger;
import org.junit.jupiter.api.Test;

public class CellsTest {

  @Test
  void testCells() {
    SearchKeyBuilder key = new SearchKeyBuilder();
    key.script(
        new Script(
            "0x9bd7e06f3ecf4be0f2fcd2188b23f1b9fcc88e5d4b65a8637b17723bbda3cce8",
            "type",
            "0x0c24d18f16e3c43272695e5db006a22cb9ddde51"));
    key.scriptType(ScriptType.lock);

    System.out.println(new Gson().toJson(key.build()));

    try {
      CellsResponse cells =
          CkbIndexerFactory.getApi()
              .getCells(key.build(), "asc", "0x" + new BigInteger("10").toString(16), null);

      System.out.println(new Gson().toJson(cells));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
