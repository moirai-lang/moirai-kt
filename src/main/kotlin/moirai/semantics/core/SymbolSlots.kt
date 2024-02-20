package moirai.semantics.core

internal sealed class RefAstSymbolSlot

internal data object RefSlotError: RefAstSymbolSlot()
internal data class RefSlotObject(val payload: ObjectType): RefAstSymbolSlot()
internal data class RefSlotPlatformObject(val payload: PlatformObjectType): RefAstSymbolSlot()
internal data class RefSlotSumObject(val payload: PlatformSumObjectType): RefAstSymbolSlot()
internal data class RefSlotSTP(val payload: StandardTypeParameter): RefAstSymbolSlot()
internal data class RefSlotLVS(val payload: LocalVariableSymbol): RefAstSymbolSlot()
internal data class RefSlotFormal(val payload: FunctionFormalParameterSymbol): RefAstSymbolSlot()
internal data class RefSlotField(val payload: FieldSymbol): RefAstSymbolSlot()

internal sealed class AssignAstSymbolSlot

internal data object AssignSlotError: AssignAstSymbolSlot()
internal data class AssignSlotLVS(val payload: LocalVariableSymbol): AssignAstSymbolSlot()

internal sealed class DotAssignAstSymbolSlot

internal data object DotAssignSlotError: DotAssignAstSymbolSlot()
internal data class DotAssignSlotField(val payload: FieldSymbol): DotAssignAstSymbolSlot()

internal sealed class DotAstSymbolSlot

internal data object DotSlotError: DotAstSymbolSlot()
internal data class DotSlotField(val payload: FieldSymbol): DotAstSymbolSlot()

internal data class DotSlotPlatformField(val payload: PlatformFieldSymbol): DotAstSymbolSlot()

internal sealed class DotApplyAstSymbolSlot

internal data object DotApplySlotError: DotApplyAstSymbolSlot()
internal data class DotApplySlotGF(val payload: GroundFunctionSymbol): DotApplyAstSymbolSlot()
internal data class DotApplySlotGMP(val payload: GroundMemberPluginSymbol): DotApplyAstSymbolSlot()
internal data class DotApplySlotSI(val payload: SymbolInstantiation): DotApplyAstSymbolSlot()

internal sealed class GroundApplyAstSymbolSlot

internal data object GroundApplySlotError: GroundApplyAstSymbolSlot()
internal data class GroundApplySlotGF(val payload: GroundFunctionSymbol): GroundApplyAstSymbolSlot()
internal data class GroundApplySlotSI(val payload: SymbolInstantiation): GroundApplyAstSymbolSlot()
internal data class GroundApplySlotTI(val payload: TypeInstantiation): GroundApplyAstSymbolSlot()
internal data class GroundApplySlotFormal(val payload: FunctionFormalParameterSymbol): GroundApplyAstSymbolSlot()
internal data class GroundApplySlotGRT(val payload: GroundRecordType): GroundApplyAstSymbolSlot()