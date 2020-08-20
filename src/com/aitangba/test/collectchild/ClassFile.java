package com.aitangba.test.collectchild;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by fhf11991 on 2020/8/19.
 */
public class ClassFile {

    private ConstPool constPool;

    private int[] interfaces;
    private int superClass;
    private int thisClass;

    public ClassFile(DataInputStream in) throws IOException {
        this.read(in);
    }

    private void read(DataInputStream in) throws IOException {
        int magic = in.readInt();
        if (magic != -889275714) {
            throw new IOException("non class file");
        } else {
            int major = in.readUnsignedShort();
            int minor = in.readUnsignedShort();

            constPool = new ConstPool(in);

            int accessFlags = in.readUnsignedShort();
            thisClass = in.readUnsignedShort();
            superClass = in.readUnsignedShort();
            int n = in.readUnsignedShort();
            int i;
            if (n == 0) {
                this.interfaces = null;
            } else {
                this.interfaces = new int[n];

                for (i = 0; i < n; ++i) {
                    this.interfaces[i] = in.readUnsignedShort();
                }
            }
        }
    }

    public String getName() {
        return this.constPool.getClassInfo(this.thisClass);
    }

    public String getSuperclass() {
        return this.constPool.getClassInfo(this.superClass);
    }

    public String[] getInterfaces() {
        if (this.interfaces == null) {
            return new String[0];
        } else {
            int n = this.interfaces.length;
            String[] list = new String[n];

            for (int i = 0; i < n; ++i) {
                list[i] = this.constPool.getClassInfo(this.interfaces[i]);
            }

            return list;
        }
    }
}
