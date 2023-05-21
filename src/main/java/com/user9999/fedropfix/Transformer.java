package com.user9999.fedropfix;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;


public class Transformer implements IClassTransformer {

    public static HashMap<Class, Boolean> horseCache = new HashMap<>();
    public static HashMap<Class, Boolean> squidCache = new HashMap<>();
    public static HashMap<Class, Boolean> batCache = new HashMap<>();
    public static HashMap<Class, Boolean> sheepCache = new HashMap<>();

    public static boolean checkForHorse(EntityLivingBase entity) {
        return horseCache.computeIfAbsent(entity.getClass(), (k) -> k.getName().toLowerCase().contains("horse"));
    }

    public static boolean checkForSquid(EntityLivingBase entity) {
        return squidCache.computeIfAbsent(entity.getClass(), (k) -> k.getName().toLowerCase().contains("squid"));
    }

    public static boolean checkForBat(EntityLivingBase entity) {
        return batCache.computeIfAbsent(entity.getClass(), (k) -> k.getName().toLowerCase().contains("bat"));
    }

    public static boolean checkForSheep(EntityLivingBase entity) {
        return sheepCache.computeIfAbsent(entity.getClass(), (k) -> k.getName().toLowerCase().contains("sheep"));
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!name.equals("lellson.foodexpansion.DropHandler")) return basicClass;
        ClassNode cn = new ClassNode();
        ClassReader r = new ClassReader(basicClass);
        r.accept(cn, 0);
        for (MethodNode method : cn.methods) {
            if (method.name.equals("onEntityDrop")) {
                System.out.println("Found on entity drop");
                for (int i = 0; i < method.instructions.size(); i++) {
                    AbstractInsnNode instruction = method.instructions.get(i);
                    if (instruction instanceof TypeInsnNode && instruction.getOpcode() == Opcodes.INSTANCEOF) {
                        switch (((TypeInsnNode) instruction).desc) {
                            case "net/minecraft/entity/passive/EntityHorse":
                                method.instructions.set(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/user9999/fedropfix/Transformer", "checkForHorse", "(Lnet/minecraft/entity/EntityLivingBase;)Z", false));
                                break;
                            case "net/minecraft/entity/passive/EntitySquid":
                                method.instructions.set(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/user9999/fedropfix/Transformer", "checkForSquid", "(Lnet/minecraft/entity/EntityLivingBase;)Z", false));
                                break;
                            case "net/minecraft/entity/passive/EntityBat":
                                method.instructions.set(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/user9999/fedropfix/Transformer", "checkForBat", "(Lnet/minecraft/entity/EntityLivingBase;)Z", false));
                                break;
                            case "net/minecraft/entity/passive/EntitySheep":
                                method.instructions.set(instruction, new MethodInsnNode(Opcodes.INVOKESTATIC, "com/user9999/fedropfix/Transformer", "checkForSheep", "(Lnet/minecraft/entity/EntityLivingBase;)Z", false));
                                break;
                        }
                    }
                }
            }
        }
        ClassWriter w = new ClassWriter(r, 0);
        cn.accept(w);
        return w.toByteArray();
    }
}
