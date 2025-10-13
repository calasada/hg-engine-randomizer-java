package app;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Move {

    public String move_name;
    public boolean implemented;
    public boolean status;
    public Type type;

    @Override public String toString() {
        return move_name + "= type:" + type + ", implemented:" + implemented + ", status:" + status;
    }

    public static String buildRandomTMList(int amount) throws IOException {

        // load tms from tmlist.json
        List<String> tmList = new ObjectMapper().readValue(
            Thread.currentThread().getContextClassLoader().getResourceAsStream("tmdata.json"),
            new TypeReference<List<String>>() {}
        );

        if (tmList.isEmpty()) {
            System.out.println("tmList is empty");
            System.exit(6);
        } else {
            //System.out.println(tmList);
        }

        StringBuilder builder = new StringBuilder();
        builder.append("    ");

        for (int i = 0; i < amount; i++) {
            builder.append(Area.takeRandom(tmList)).append(", ");
            if((i+1) % 5 == 0) {
                builder.append(System.lineSeparator()).append("    ");
            }
        }
        
        builder.append("0xFFFF").append(System.lineSeparator());

        return builder.toString();
        
    }

}
