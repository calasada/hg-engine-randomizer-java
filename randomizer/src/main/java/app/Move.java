package app;

import java.io.IOException;
import java.util.List;

public class Move {

    public String move_name;
    public boolean implemented;
    public MoveSplit split;
    public Type type;

    public enum MoveSplit {
        SPLIT_PHYSICAL,
        SPLIT_SPECIAL,
        SPLIT_STATUS
    }

    @Override public String toString() {
        return move_name + "= type:" + type + ", implemented:" + implemented + ", status:" + split;
    }

    public static String buildDepartmentTMList(List<String> tmList, int amount) throws IOException {

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

    public static String buildCityTMList(List<String> tmList, List<String> itemList) throws IOException {

        if (tmList.isEmpty()) {
            System.out.println("tmList is empty");
            System.exit(6);
        } else {
            //System.out.println(tmList);
        }

        if (itemList.isEmpty()) {
            System.out.println("itemList is empty");
            System.exit(6);
        } else {
            //System.out.println(itemList);
        }

        StringBuilder builder = new StringBuilder();
        builder.append("    ");

        for (int i = 0; i < 12; i++) {
            // add item or tm based on index, 7 items 5 tms
            if(i < 7) {
                builder.append(Area.takeRandom(itemList)).append(", ");
            } else {
                builder.append(Area.takeRandom(tmList)).append(", ");
            }
            
            // new line every 5 entries
            if((i+1) % 5 == 0) {
                builder.append(System.lineSeparator()).append("    ");
            }
        }
        
        builder.append("0xFFFF").append(System.lineSeparator());

        return builder.toString();
        
    }

}
