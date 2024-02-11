package moirai.composition

interface SourceStore {
    fun fetchSourceText(namespace: List<String>): String
}