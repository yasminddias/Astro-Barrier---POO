package engine;

import gameobject.IGameObject;
import gameobject.geometry.Point;
import gameobject.transform.ITransform;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator; // Adicionado para remoção segura

/**
 * Implementação principal do motor de jogo (IGameEngine).
 * Gere as listas de objetos de jogo ativos e inativos, o ciclo de jogo (game loop),
 * atualizações de comportamento, deteção de colisões e restrição de objetos aos limites da área de jogo.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv A lista 'enabled' nunca é nula e contém todos os IGameObjects ativos.
 * @inv A lista 'disabled' nunca é nula e contém todos os IGameObjects inativos.
 * @inv 'bounds' define a área retangular de jogo e nunca é nulo após a construção (tem um valor padrão).
 */
public class GameEngine {
    private final List<IGameObject> enabled = new ArrayList<>();
    private final List<IGameObject> disabled = new ArrayList<>();
    private Rectangle bounds;

    // Listas para adiamento de operações para evitar ConcurrentModificationException
    private final List<IGameObject> toAddEnabled = new ArrayList<>();
    private final List<IGameObject> toEnable = new ArrayList<>();
    private final List<IGameObject> toDisable = new ArrayList<>();
    private final List<IGameObject> toDestroy = new ArrayList<>();

    /**
     * Constrói uma nova instância de GameEngine.
     * Inicializa os limites (bounds) da área de jogo com um valor padrão.
     * @post Um novo GameEngine é criado com bounds padrão (0,0, 300,600). As listas de objetos estão vazias.
     */
    public GameEngine() {
        this.bounds = new Rectangle(0, 0, 300, 600);
    }

    /**
     * Define os limites (bounds) da área de jogo.
     * @param bounds O novo retângulo que define os limites. Não deve ser nulo.
     * @post Os 'bounds' do motor de jogo são atualizados para o 'bounds' fornecido.
     */
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    /**
     * Adiciona um IGameObject à lista de espera para ser ativado no próximo ciclo de processamento.
     * O objeto é associado a este motor e o seu comportamento é inicializado.
     * @param go O IGameObject a ser adicionado e ativado. Não deve ser nulo.
     * @post 'go' é adicionado à lista toAddEnabled para processamento posterior.
     * @post O motor de jogo ('this') é definido no 'go'.
     * @post O comportamento de 'go' é vinculado ao motor de jogo.
     */
    public void addEnabled(IGameObject go) {
        if (go != null && !enabled.contains(go) && !toAddEnabled.contains(go)) {
            go.setEngine(this); // Associa o motor ao GO
            if (go.behaviour() != null) {
                go.behaviour().linkGameEngine(this); // Vincula o comportamento ao motor
            }
            toAddEnabled.add(go);
        }
    }

    /**
     * Adiciona um IGameObject à lista de objetos inativos (disabled).
     * O seu comportamento é inicializado e o método onDisabled() é chamado.
     * (Nota: A adição direta a 'disabled' é mantida, pois não costuma causar problemas de concorrência da mesma forma que 'enabled'.)
     * @param go O IGameObject a ser adicionado como inativo. Não deve ser nulo.
     * @post Se 'go' não estiver já em 'disabled', é adicionado.
     * @post O motor de jogo ('this') é definido no 'go'.
     * @post O comportamento de 'go' é vinculado ao motor e os seus métodos onInit() e onDisabled() são chamados.
     */
    public void addDisabled(IGameObject go) {
        if (go != null && !disabled.contains(go)) {
            go.setEngine(this);
            if (go.behaviour() != null) {
                go.behaviour().linkGameEngine(this);
                go.behaviour().onInit();
                go.behaviour().onDisabled();
            }
            disabled.add(go);
        }
    }

    /**
     * Adiciona um IGameObject à lista de espera para ser ativado (movido de inativo para ativo) no próximo ciclo.
     * @param go O IGameObject a ser ativado. Não deve ser nulo.
     * @post 'go' é adicionado à lista toEnable.
     */
    public void enable(IGameObject go) {
        if (go != null && disabled.contains(go) && !toEnable.contains(go)) {
            toEnable.add(go);
        }
    }

    /**
     * Adiciona um IGameObject à lista de espera para ser desativado (movido de ativo para inativo) no próximo ciclo.
     * @param go O IGameObject a ser desativado. Não deve ser nulo.
     * @post 'go' é adicionado à lista toDisable.
     */
    public void disable(IGameObject go) {
        if (go != null && enabled.contains(go) && !toDisable.contains(go)) {
            toDisable.add(go);
        }
    }

    /**
     * Adiciona um IGameObject à lista de espera para ser destruído no próximo ciclo.
     * @param go O IGameObject a ser destruído. Não deve ser nulo.
     * @post 'go' é adicionado à lista toDestroy.
     */
    public void destroy(IGameObject go) {
        if (go != null && !toDestroy.contains(go)) {
            toDestroy.add(go);
        }
    }

    /**
     * Destrói todos os IGameObjects (ativos e inativos), adicionando-os à lista de espera para destruição.
     * @post Todos os objetos em 'enabled' e 'disabled' são adicionados à lista toDestroy.
     */
    public void destroyAll() {
        for (IGameObject go : new ArrayList<>(enabled)) { // Itera sobre cópia para evitar CME se toDestroy fosse processado imediatamente
            if (!toDestroy.contains(go)) toDestroy.add(go);
        }
        for (IGameObject go : new ArrayList<>(disabled)) {
            if (!toDestroy.contains(go)) toDestroy.add(go);
        }
    }

    /**
     * Processa as listas de espera (adição, ativação, desativação, destruição) de GameObjects.
     * Este método é chamado internamente, tipicamente no final do ciclo 'run'.
     * @post As operações pendentes nas listas toAddEnabled, toEnable, toDisable e toDestroy são executadas.
     */
    private void processPendingOperations() {
        // Adicionar e ativar novos
        for (IGameObject go : toAddEnabled) {
            if (!enabled.contains(go)) {
                enabled.add(go);
                if (go.behaviour() != null) {
                    go.behaviour().onInit(); // Chamado aqui para garantir que o engine está definido
                    go.behaviour().onEnabled();
                }
            }
        }
        toAddEnabled.clear();

        // Ativar existentes
        for (IGameObject go : toEnable) {
            if (disabled.remove(go)) {
                if (!enabled.contains(go)) {
                    enabled.add(go);
                    if (go.behaviour() != null) {
                        go.behaviour().onEnabled();
                    }
                }
            }
        }
        toEnable.clear();

        // Desativar existentes
        for (IGameObject go : toDisable) {
            if (enabled.remove(go)) {
                if (!disabled.contains(go)) {
                    disabled.add(go);
                    if (go.behaviour() != null) {
                        go.behaviour().onDisabled();
                    }
                }
            }
        }
        toDisable.clear();

        // Destruir
        for (IGameObject go : toDestroy) {
            boolean removedFromEnabled = enabled.remove(go);
            boolean removedFromDisabled = disabled.remove(go);
            toAddEnabled.remove(go); // Remover também das listas de adição/ativação pendentes
            toEnable.remove(go);

            if (removedFromEnabled || removedFromDisabled) {
                if (go.behaviour() != null) {
                    go.behaviour().onDestroy();
                }
            }
        }
        toDestroy.clear();
    }


    /**
     * Executa um único passo do ciclo de jogo (game loop).
     * Atualiza todos os objetos de jogo ativos, processa inputs, verifica colisões e, finalmente,
     * processa operações pendentes de gestão de objetos (adição, remoção, etc.).
     * @param dt O tempo decorrido desde o último passo do ciclo (delta time), em segundos. Deve ser não negativo.
     * @param input O estado atual dos inputs do jogo. Pode ser nulo se não houver inputs a processar.
     * @post Todos os IGameObjects em 'enabled' são atualizados (comportamento, colisor).
     * @post As colisões entre objetos ativos são verificadas e tratadas.
     * @post Os objetos ativos são restringidos aos 'bounds' do motor, se aplicável pela sua lógica.
     * @post Todas as operações pendentes de adição, remoção, ativação e desativação de GameObjects são executadas.
     */
    public void run(double dt, IInputEvent input) {
        // Usar Iterator para permitir remoção segura durante a iteração, se necessário,
        // ou iterar sobre uma cópia da lista para atualizações. A cópia é mais simples.
        List<IGameObject> currentEnabledObjects = new ArrayList<>(enabled);

        for (IGameObject go : currentEnabledObjects) {
            // Verifica se o objeto ainda está na lista principal 'enabled'
            // (pode ter sido marcado para destruição ou desativação por outro objeto no mesmo frame)
            if (enabled.contains(go) && !toDestroy.contains(go) && !toDisable.contains(go)) {
                if (go.behaviour() != null) {
                    go.behaviour().onUpdate(dt, input);
                }
                if (go.collider() != null) {
                    go.collider().onUpdate();
                }
                // ClampToBounds pode ser chamado aqui ou dentro de onUpdate do comportamento, se específico.
                // Se for uma regra geral do motor, aqui é apropriado.
                clampToBounds(go); // Descomentado, pois parece ser uma função geral do motor
            }
        }
        checkCollisions(); // Verifica colisões após todas as atualizações de posição
        processPendingOperations(); // Processa adições, remoções, etc., no final do ciclo
    }


    /**
     * Verifica e processa colisões entre todos os objetos de jogo ativos.
     * Quando uma colisão é detetada, o método onCollision() dos comportamentos dos objetos envolvidos é chamado.
     * Itera sobre uma cópia da lista de objetos ativos para evitar problemas de modificação concorrente se
     * as callbacks de colisão modificarem as listas de objetos do motor.
     * @post Os métodos onCollision() dos IBehaviours dos objetos em 'enabled' que colidiram são invocados.
     */
    public void checkCollisions() {
        List<IGameObject> gameObjectsToCheck = new ArrayList<>(enabled); // Trabalha com uma cópia
        int size = gameObjectsToCheck.size();

        for (int i = 0; i < size; i++) {
            IGameObject a = gameObjectsToCheck.get(i);
            // Garante que 'a' ainda está ativo e não foi marcado para destruição/desativação neste ciclo
            if (!enabled.contains(a) || toDestroy.contains(a) || toDisable.contains(a)) continue;

            for (int j = i + 1; j < size; j++) {
                IGameObject b = gameObjectsToCheck.get(j);
                // Garante que 'b' ainda está ativo e não foi marcado para destruição/desativação
                if (!enabled.contains(b) || toDestroy.contains(b) || toDisable.contains(b)) continue;

                if (a.collider() != null && b.collider() != null && a.collider().isColliding(b.collider())) {
                    // System.out.println("Colisão detectada entre " + a.name() + " e " + b.name()); // Linha de depuração
                    if (a.behaviour() != null) a.behaviour().onCollision(List.of(b));
                    if (b.behaviour() != null) b.behaviour().onCollision(List.of(a));
                    // As consequências da colisão (ex: destruição de 'a' ou 'b')
                    // serão tratadas pelas suas lógicas de onCollision e processadas por processPendingOperations.
                }
            }
        }
    }

    /**
     * Restringe um objeto de jogo aos limites (bounds) definidos para o motor de jogo.
     * Ajusta a posição do objeto se ele estiver fora dos limites.
     * (Nota: Esta é uma restrição simples ao centro do objeto. Dependendo do tamanho e forma do objeto,
     * pode ser necessário um tratamento mais sofisticado para evitar que partes do objeto saiam dos limites.)
     * @param go O IGameObject a ser restringido. Não deve ser nulo e deve ter uma transformação válida.
     * @post A posição da transformação de 'go' é ajustada para estar dentro dos 'bounds' do motor.
     */
    private void clampToBounds(IGameObject go) {
        if (go == null || go.transform() == null || bounds == null) return;

        ITransform t = go.transform();
        Point pos = t.position();

        double currentX = pos.getX();
        double currentY = pos.getY();

        double minX = bounds.getMinX();
        double maxX = bounds.getMaxX();
        double minY = bounds.getMinY();
        double maxY = bounds.getMaxY();

        double newX = currentX;
        double newY = currentY;

        // Assumindo que a posição é o centro. Se for canto superior esquerdo, a lógica de maxX/maxY precisa de ajuste.
        // A lógica original parece assumir que a posição é o centro e não considera o tamanho do objeto.
        // Para uma restrição mais precisa, seria necessário o tamanho/raio do objeto.
        // Exemplo: newX = Math.max(minX + objectWidth/2, Math.min(currentX, maxX - objectWidth/2));

        if (currentX < minX) newX = minX;
        else if (currentX > maxX) newX = maxX;

        if (currentY < minY) newY = minY;
        else if (currentY > maxY) newY = maxY;

        if (newX != currentX || newY != currentY) {
            pos.set(newX, newY); // Define a nova posição diretamente
        }
    }

    /**
     * Devolve uma cópia da lista de IGameObjects ativos.
     * @return Uma nova ArrayList contendo todos os IGameObjects atualmente na lista 'enabled'.
     */
    public List<IGameObject> getEnabled() { return new ArrayList<>(enabled); }

    /**
     * Devolve uma cópia da lista de IGameObjects inativos.
     * @return Uma nova ArrayList contendo todos os IGameObjects atualmente na lista 'disabled'.
     */
    public List<IGameObject> getDisabled() { return new ArrayList<>(disabled); }

    /**
     * Devolve os limites (bounds) atuais da área de jogo.
     * @return Um objeto Rectangle representando os limites.
     */
    public Rectangle getBounds() {
        return bounds;
    }
}