package org.raku.nqp.sixmodel.reprs;


import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.raku.nqp.runtime.ThreadContext;
import org.raku.nqp.sixmodel.REPR;
import org.raku.nqp.sixmodel.STable;
import org.raku.nqp.sixmodel.SerializationReader;
import org.raku.nqp.sixmodel.SerializationWriter;
import org.raku.nqp.sixmodel.SixModelObject;
import org.raku.nqp.sixmodel.StorageSpec;
import org.raku.nqp.sixmodel.TypeObject;

public class P6str extends REPR {
    private static final StorageSpec ss = new StorageSpec();
    static {
        ss.inlineable = StorageSpec.INLINED;
        ss.boxed_primitive = StorageSpec.BP_STR;
        ss.can_box = StorageSpec.CAN_BOX_STR;
    }

    @Override
    public SixModelObject type_object_for(ThreadContext tc, SixModelObject HOW) {
        STable st = new STable(this, HOW);
        SixModelObject obj = new TypeObject();
        obj.st = st;
        st.WHAT = obj;
        return st.WHAT;
    }

    @Override
    public SixModelObject allocate(ThreadContext tc, STable st) {
        P6strInstance obj = new P6strInstance();
        obj.st = st;
        obj.value = "";
        return obj;
    }

    @Override
    public StorageSpec get_storage_spec(ThreadContext tc, STable st) {
        return ss;
    }

    @Override
    public void inlineStorage(ThreadContext tc, STable st, ClassWriter cw, String prefix) {
        cw.visitField(Opcodes.ACC_PUBLIC, prefix, "Ljava/lang/String;", null, null);
    }

    @Override
    public void inlineBind(ThreadContext tc, STable st, MethodVisitor mv, String className, String prefix) {
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitInsn(Opcodes.ICONST_0 + ThreadContext.NATIVE_STR);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "org/raku/nqp/runtime/ThreadContext", "native_type", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitFieldInsn(Opcodes.GETFIELD, "org/raku/nqp/runtime/ThreadContext", "native_s", "Ljava/lang/String;");
        mv.visitFieldInsn(Opcodes.PUTFIELD, className, prefix, "Ljava/lang/String;");
        mv.visitInsn(Opcodes.RETURN);
    }

    @Override
    public void inlineGet(ThreadContext tc, STable st, MethodVisitor mv, String className, String prefix) {
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitInsn(Opcodes.DUP);
        mv.visitInsn(Opcodes.ICONST_0 + ThreadContext.NATIVE_STR);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "org/raku/nqp/runtime/ThreadContext", "native_type", "I");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, className, prefix, "Ljava/lang/String;");
        mv.visitFieldInsn(Opcodes.PUTFIELD, "org/raku/nqp/runtime/ThreadContext", "native_s", "Ljava/lang/String;");
        mv.visitInsn(Opcodes.RETURN);
    }

    @Override
    public void inlineDeserialize(ThreadContext tc, STable st, MethodVisitor mv, String className, String prefix) {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 3);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "org/raku/nqp/sixmodel/SerializationReader", "readStr", "()Ljava/lang/String;");
        mv.visitFieldInsn(Opcodes.PUTFIELD, className, prefix, "Ljava/lang/String;");
    }

    @Override
    public void generateBoxingMethods(ThreadContext tc, STable st, ClassWriter cw, String className, String prefix) {
        MethodVisitor getMeth = cw.visitMethod(Opcodes.ACC_PUBLIC, "get_str",
                "(Lorg/raku/nqp/runtime/ThreadContext;)Ljava/lang/String;", null, null);
        getMeth.visitVarInsn(Opcodes.ALOAD, 0);
        getMeth.visitFieldInsn(Opcodes.GETFIELD, className, prefix, "Ljava/lang/String;");
        getMeth.visitInsn(Opcodes.ARETURN);
        getMeth.visitMaxs(0, 0);

        MethodVisitor setMeth = cw.visitMethod(Opcodes.ACC_PUBLIC, "set_str",
                "(Lorg/raku/nqp/runtime/ThreadContext;Ljava/lang/String;)V", null, null);
        setMeth.visitVarInsn(Opcodes.ALOAD, 0);
        setMeth.visitVarInsn(Opcodes.ALOAD, 2);
        setMeth.visitFieldInsn(Opcodes.PUTFIELD, className, prefix, "Ljava/lang/String;");
        setMeth.visitInsn(Opcodes.RETURN);
        setMeth.visitMaxs(0, 0);
    }

    // We don't depend on any details of the STable, so no description is needed
    @Override public boolean inline_description(ThreadContext tc, STable st, StringBuilder out) {
        return true;
    }
    @Override public boolean box_description(ThreadContext tc, STable st, StringBuilder out) {
        return true;
    }

    @Override
    public SixModelObject deserialize_stub(ThreadContext tc, STable st) {
        P6strInstance obj = new P6strInstance();
        obj.st = st;
        return obj;
    }

    @Override
    public void deserialize_finish(ThreadContext tc, STable st,
                                   SerializationReader reader, SixModelObject obj) {
        ((P6strInstance)obj).value = reader.readStr();
    }

    @Override
    public void serialize(ThreadContext tc, SerializationWriter writer, SixModelObject obj) {
        writer.writeStr(((P6strInstance)obj).value);
    }

    @Override
    public void serialize_inlined(ThreadContext tc, STable st, SerializationWriter writer,
                                  String prefix, SixModelObject obj) {
        try {
            writer.writeStr((String)obj.getClass().getField(prefix).get(obj));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
