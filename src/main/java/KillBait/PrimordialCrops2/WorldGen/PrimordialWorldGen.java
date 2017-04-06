package KillBait.PrimordialCrops2.WorldGen;

import KillBait.PrimordialCrops2.Registry.ModBlocks;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

/**
 * Created by Jon on 19/10/2016.
 */
public class PrimordialWorldGen implements IWorldGenerator {

	private WorldGenerator MinicioOre;
	private WorldGenerator AccioOre;
	private WorldGenerator CrucioOre;

	public PrimordialWorldGen() {
		this.MinicioOre = new WorldGenMinable(ModBlocks.oreMinicio.getDefaultState(), 6, BlockMatcher.forBlock(Blocks.STONE));
		this.AccioOre = new WorldGenMinable(ModBlocks.oreAccio.getDefaultState(), 4, BlockMatcher.forBlock(Blocks.NETHERRACK));
		this.CrucioOre = new WorldGenMinable(ModBlocks.oreCrucio.getDefaultState(), 2, BlockMatcher.forBlock(Blocks.END_STONE));

	}

	private void runGenerator(WorldGenerator generator, World world, Random rand, int chunk_X, int chunk_Z, int chancesToSpawn, int minHeight, int maxHeight) {
		if (minHeight < 0 || maxHeight > 256 || minHeight > maxHeight)
			throw new IllegalArgumentException("Illegal Height Arguments for WorldGenerator");

		int heightDiff = maxHeight - minHeight + 1;
		for (int i = 0; i < chancesToSpawn; i++) {
			int x = chunk_X * 16 + rand.nextInt(16);
			int y = minHeight + rand.nextInt(heightDiff);
			int z = chunk_Z * 16 + rand.nextInt(16);
			generator.generate(world, rand, new BlockPos(x, y, z));
		}
	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
						 IChunkProvider chunkProvider) {

		switch (world.provider.getDimension()) {
			case -1: //Nether
				this.runGenerator(this.AccioOre, world, random, chunkX, chunkZ, 5, 0, 123);
				break;
			case 1: //End
				this.runGenerator(this.CrucioOre, world, random, chunkX, chunkZ, 5, 0, 64);
				break;
			default:
				this.runGenerator(this.MinicioOre, world, random, chunkX, chunkZ, 20, 20, 64);
				break;
		}

	}
}
