package gameobject.behaviour;

import engine.GameEngine;
import engine.IInputEvent;
import gameobject.IGameObject;
import gameobject.entity.Bullet;
import gameobject.geometry.Point;
import gameobject.transform.ITransform;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;

/**
 * Define o comportamento da nave controlada pelo jogador.
 * Responsável pelo movimento horizontal da nave com base nos inputs do teclado,
 * pela lógica de disparo de projéteis e pela gestão da contagem de projéteis.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv O IGameObject associado (gameObject) deve existir para o comportamento funcionar.
 * @inv 'engine' deve ser uma referência válida ao GameEngine para disparo de projéteis e verificação de limites.
 * @inv currentBulletCount está sempre entre 0 e MAX_BULLETS.
 */
public class PlayerBehaviour implements IBehaviour {
    private IGameObject gameObject;
    private GameEngine engine;
    private boolean spaceBarWasPressedLastFrame = false;
    public static final int MAX_BULLETS = 6;
    private int currentBulletCount = MAX_BULLETS;
    private int bulletFireCounter = 0; // Contador para nomes únicos de projéteis
    private double playerWidth = 20; // Largura estimada do jogador, usada para limites (hardcoded)

    /**
     * Construtor padrão para PlayerBehaviour.
     * @post O estado inicial de controlo de disparo (spaceBarWasPressedLastFrame) é false.
     * @post A contagem de projéteis é inicializada para MAX_BULLETS.
     */
    public PlayerBehaviour() {
    }

    /**
     * Define a referência ao GameEngine para este comportamento.
     * (Método privado usado internamente por linkGameEngine).
     * @param engine O GameEngine a ser associado.
     * @post A referência 'engine' interna é definida.
     */
    private void setGameEngine(GameEngine engine) {
        this.engine = engine;
    }

    /**
     * Devolve o IGameObject ao qual este comportamento está associado.
     * @return O IGameObject associado.
     */
    @Override
    public IGameObject gameObject() {
        return gameObject;
    }

    /**
     * Associa este comportamento a um IGameObject.
     * @param go O IGameObject a associar.
     * @post O campo 'gameObject' é definido para o IGameObject fornecido.
     */
    @Override
    public void gameObject(IGameObject go) {
        this.gameObject = go;
    }

    /**
     * Inicializa o estado do comportamento do jogador.
     * Reinicia a contagem de projéteis e calcula a 'playerWidth' com base no colisor do GameObject.
     * @post A contagem de projéteis é redefinida através de resetPlayerSpecificState().
     * @post 'playerWidth' é atualizado com base na dimensão característica do colisor do 'gameObject', se disponível.
     */
    @Override
    public void onInit() {
        resetPlayerSpecificState();

        if (gameObject != null && gameObject.collider() != null) {
            playerWidth = gameObject.collider().getCharacteristicDimension() * 2;
        }
    }

    /**
     * Chamado quando o GameObject do jogador é ativado. Não realiza ações específicas.
     * @post Nenhum estado é alterado.
     */
    @Override
    public void onEnabled() {
    }

    /**
     * Chamado quando o GameObject do jogador é desativado. Não realiza ações específicas.
     * @post Nenhum estado é alterado.
     */
    @Override
    public void onDisabled() {
    }

    /**
     * Chamado quando o GameObject do jogador é destruído. Não realiza ações específicas.
     * @post Nenhum estado é alterado.
     */
    @Override
    public void onDestroy() {
    }

    /**
     * Atualiza o estado do jogador em cada frame.
     * Processa inputs do teclado para movimento horizontal e disparo de projéteis.
     * Garante que o jogador permanece dentro dos limites horizontais definidos.
     * @param dt O tempo decorrido desde a última atualização (delta time), em segundos. Deve ser não negativo.
     * @param ie O estado atual dos inputs do jogo (ex: teclas premidas). Não deve ser nulo.
     * @post A posição X do 'gameObject' é atualizada com base nas teclas Esquerda/Direita, 'speed' e 'dt', restringida aos limites.
     * @post Se a tecla Espaço for premida (e não estava premida no frame anterior), um projétil é disparado (se houver projéteis disponíveis).
     * @post 'spaceBarWasPressedLastFrame' é atualizado.
     */
    @Override
    public void onUpdate(double dt, IInputEvent ie) {
        if (gameObject == null || engine == null || ie == null) {
            return;
        }

        double speed = 250 * dt;
        double dx = 0;

        if (ie.isKeyPressed(KeyEvent.VK_LEFT)) dx -= speed;
        if (ie.isKeyPressed(KeyEvent.VK_RIGHT)) dx += speed;

        ITransform t = gameObject.transform();
        Point currentPosition = t.position();

        double newX = currentPosition.getX() + dx;

        // Limites hardcoded. Idealmente, deveriam ser mais dinâmicos ou baseados nos limites do GameEngine.
        double minX = 35;
        double maxX = 370;

        newX = Math.max(minX, Math.min(maxX, newX));

        double deltaX = newX - currentPosition.getX();

        t.move(new Point(deltaX, 0), 0);

        boolean spaceBarIsCurrentlyPressed = ie.isKeyPressed(KeyEvent.VK_SPACE);
        if (spaceBarIsCurrentlyPressed && !spaceBarWasPressedLastFrame) {
            shoot();
        }
        spaceBarWasPressedLastFrame = spaceBarIsCurrentlyPressed;
    }

    /**
     * Efetua a lógica de disparo de um projétil.
     * Se o jogador tiver projéteis disponíveis, cria um novo Bullet, configura-o e adiciona-o ao motor de jogo.
     * Decrementa a contagem de projéteis.
     * @post Se 'currentBulletCount' > 0 e 'gameObject' e 'engine' não são nulos:
     * Um novo Bullet é criado na posição ligeiramente acima do jogador.
     * O motor ('engine') é associado ao novo projétil e ao seu comportamento.
     * O novo projétil é adicionado à lista de objetos ativos do motor de jogo.
     * 'currentBulletCount' é decrementado.
     * 'bulletFireCounter' é incrementado.
     */
    private void shoot() {
        if (gameObject == null || engine == null || currentBulletCount <= 0) {
            return;
        }

        ITransform playerTransform = gameObject.transform();
        Point playerPosition = playerTransform.position();

        double bulletX = playerPosition.getX();
        double bulletY = playerPosition.getY() - 25; // Posição Y ligeiramente acima do jogador

        Bullet newBullet = new Bullet("player_bullet_" + bulletFireCounter++, bulletX, bulletY);

        newBullet.setEngine(this.engine);
        if (newBullet.behaviour() != null) {
            // O GameScreen já chama linkGameEngine no PlayerBehaviour. Para o Bullet,
            // o seu GameEngine é definido através de newBullet.setEngine().
            // A BulletBehaviour não parece precisar de uma referência explícita ao engine se go.engine() funcionar.
            // A chamada linkGameEngine aqui pode ser redundante se setEngine no GameObject for suficiente
            // para que go.engine() em BulletBehaviour funcione.
            // Se BulletBehaviour.onUpdate usa go.engine(), e GameObject.setEngine define esse engine, então está ok.
            newBullet.behaviour().linkGameEngine(this.engine); // Para consistência, se necessário
        }

        engine.addEnabled(newBullet);
        currentBulletCount--;
    }

    /**
     * Devolve a contagem atual de projéteis disponíveis para o jogador.
     * @return O número de projéteis restantes.
     */
    public int getBulletCount() {
        return currentBulletCount;
    }

    /**
     * Reinicia a contagem de projéteis do jogador para o máximo permitido.
     * @post currentBulletCount é definido como MAX_BULLETS.
     */
    public void resetBulletCount() {
        this.currentBulletCount = MAX_BULLETS;
    }

    /**
     * Devolve o número máximo de projéteis que o jogador pode ter.
     * @return O valor de MAX_BULLETS.
     */
    public int getMaxBullets() {
        return MAX_BULLETS;
    }

    /**
     * Trata colisões envolvendo o jogador. Este método está vazio,
     * indicando que o jogador não reage ativamente a colisões (ex: ser destruído).
     * @param gol Lista de IGameObjects com os quais o jogador colidiu (não utilizada).
     * @post Nenhum estado é alterado.
     */
    @Override
    public void onCollision(List<IGameObject> gol) {
    }

    /**
     * Vincula o GameEngine a este comportamento do jogador.
     * @param engine O GameEngine a ser vinculado.
     * @post A referência interna 'engine' é definida através de setGameEngine(engine).
     */
    @Override
    public void linkGameEngine(GameEngine engine) {
        this.setGameEngine(engine);
    }

    /**
     * Devolve a contagem atual de projéteis para exibição no HUD.
     * @return O valor de getBulletCount().
     */
    @Override
    public int getDisplayBulletCount() {
        return getBulletCount();
    }

    /**
     * Devolve a contagem máxima de projéteis para exibição no HUD.
     * @return O valor de getMaxBullets().
     */
    @Override
    public int getMaxDisplayBullets() {
        return getMaxBullets();
    }

    /**
     * Reinicia o estado específico do jogador, primariamente a contagem de projéteis.
     * @post A contagem de projéteis é redefinida através de resetBulletCount().
     */
    @Override
    public void resetPlayerSpecificState() {
        resetBulletCount();
    }
}