package org.shardscript.semantics.core

sealed class RefAstSymbolSlot

data object RefSlotError: RefAstSymbolSlot()
data class RefSlotBasic(val payload: BasicTypeSymbol): RefAstSymbolSlot()
data class RefSlotObject(val payload: ObjectSymbol): RefAstSymbolSlot()
data class RefSlotPlatformObject(val payload: PlatformObjectSymbol): RefAstSymbolSlot()
data class RefSlotSTP(val payload: StandardTypeParameter): RefAstSymbolSlot()
data class RefSlotTI(val payload: TypeInstantiation): RefAstSymbolSlot()
data class RefSlotLVS(val payload: LocalVariableSymbol): RefAstSymbolSlot()
data class RefSlotFormal(val payload: FunctionFormalParameterSymbol): RefAstSymbolSlot()
data class RefSlotField(val payload: FieldSymbol): RefAstSymbolSlot()