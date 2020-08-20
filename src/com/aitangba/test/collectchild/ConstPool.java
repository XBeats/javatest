package com.aitangba.test.collectchild;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by fhf11991 on 2020/8/20.
 */
public class ConstPool {

    private HashMap<Integer, ConstInfo> constInfoHashMap = new HashMap<>();

    public ConstPool(DataInputStream in) throws IOException {
        this.read(in);
    }

    public String getClassInfo(int index) {
        ClassInfo c = (ClassInfo) constInfoHashMap.get(index);
        return c == null?null: toJavaName(this.getUtf8Info(c.getValue()));
    }

    public String getUtf8Info(int index) {
        Utf8Info utf = (Utf8Info)constInfoHashMap.get(index);
        return utf.getValue();
    }

    private String toJavaName(String classname) {
        return classname.replace('/', '.');
    }

    private void read(DataInputStream in) throws IOException {
        int n = in.readUnsignedShort();

        while (true) {
            int tag;
            do {
                --n;
                if (n <= 0) {
                    return;
                }

                tag = this.readOne(in);
            } while (tag != 5 && tag != 6);
            // const info padding
            index++;
            --n;
        }
    }

    public static final int CONST_Utf8 = 1;
    public static final int CONST_Unknown = 2;
    public static final int CONST_Integer = 3;
    public static final int CONST_Float = 4;
    public static final int CONST_Long = 5;
    public static final int CONST_Double = 6;
    public static final int CONST_Class = 7;
    public static final int CONST_String = 8;
    public static final int CONST_Fieldref = 9;
    public static final int CONST_Methodref = 10;
    public static final int CONST_InterfaceMethodref = 11;
    public static final int CONST_NameAndType = 12;

    private int index = 1; // index 从1开始

    private int readOne(DataInputStream in) throws IOException {
        int tag = in.readUnsignedByte();
        switch (tag) {
            case CONST_Utf8: // utf-8
                constInfoHashMap.put(index, new Utf8Info(in.readUTF()));
                break;
            case CONST_Integer:
                in.readInt();
                break;
            case CONST_Float:
                in.readFloat();
                break;
            case CONST_Long:
                in.readLong();
                break;
            case CONST_Double:
                in.readDouble();
                break;
            case CONST_Class: // class
                constInfoHashMap.put(index, new ClassInfo(in.readUnsignedShort()));
                break;
            case CONST_String:
                in.readUnsignedShort();
                break;
            case CONST_Fieldref:
                int classIndex = in.readUnsignedShort();
                int nameAndTypeIndex = in.readUnsignedShort();
                break;
            case CONST_Methodref:
                classIndex = in.readUnsignedShort();
                nameAndTypeIndex = in.readUnsignedShort();
                break;
            case CONST_InterfaceMethodref:
                classIndex = in.readUnsignedShort();
                nameAndTypeIndex = in.readUnsignedShort();
                break;
            case CONST_NameAndType:
                int memberName = in.readUnsignedShort();
                int typeDescriptor = in.readUnsignedShort();
                break;
            case CONST_Unknown:
            default:
                throw new IOException("invalid constant type: " + tag);
        }

        index++;
        return tag;
    }

}
