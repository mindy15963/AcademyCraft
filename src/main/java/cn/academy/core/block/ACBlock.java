package cn.academy.core.block;

import cn.academy.core.AcademyCraft;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * @author WeAthFolD
 */
public class ACBlock extends Block {

    public ACBlock(String name, Material mat) {
        super(mat);
        setCreativeTab(AcademyCraft.cct);
        // TODO: Implement in json
//        setBlockTextureName("academy:" + name);
//        setBlockName("ac_" + name);
    }

}
