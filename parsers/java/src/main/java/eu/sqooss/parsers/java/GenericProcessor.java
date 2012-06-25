package eu.sqooss.parsers.java;


import java.util.Map;
import java.util.HashMap;
import org.antlr.runtime.tree.Tree;

public class GenericProcessor<T> implements TokenProcessor {

    T results;
    
    private Map<Integer, TokenProcessor> tokenProcessorTable =
            new HashMap<Integer, TokenProcessor>();

    protected GenericProcessor() {
    }

    public T getResults() {
        return this.results;
    }
    
    public void setResults(T results) {
        this.results = results;
    }
    
    protected TokenProcessor addToTable(Integer tokenType,
            TokenProcessor tokenProcessor) {
        if (tokenProcessorTable.containsKey(tokenType)) {
            tokenProcessorTable.remove(tokenType);
        }
        return tokenProcessorTable.put(tokenType, tokenProcessor);
    }

    protected TokenProcessor getTokenProcessor(Integer tokenType) {
        return tokenProcessorTable.get(tokenType);
    }

    @Override
    public void goingDown(Tree t) {
        TokenProcessor tokenProcessor = tokenProcessorTable.get(t.getType());
        if (tokenProcessor != null) {
            tokenProcessor.goingDown(t);
        }
    }

    @Override
    public void goingUp(Tree t) {
        TokenProcessor tokenProcessor = tokenProcessorTable.get(t.getType());
        if (tokenProcessor != null) {
            tokenProcessor.goingUp(t);
        }
    }
}
