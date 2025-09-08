package gameobject;

import engine.GameEngine;
import gameobject.behaviour.IBehaviour;
import gameobject.collider.ICollider;
import gameobject.geometry.Point;
import gameobject.shape.IShape;
import gameobject.transform.ITransform;
import java.util.Locale;

/**
 * Implementação base de um objeto de jogo (IGameObject).
 * Um GameObject possui um nome, uma transformação (posição, rotação, escala, camada),
 * um colisor, uma forma visual e um comportamento.
 * Esta classe agrega os componentes essenciais de qualquer entidade no jogo.
 * @author José Tico, Yasmin Dias e Guilherme Carmo
 * @version 25-05-2025
 * @inv As referências name, transform, collider, shape e behaviour nunca são nulas após a construção bem-sucedida.
 * @inv O behaviour associado tem este GameObject como seu 'gameObject()'.
 */
public class GameObject implements IGameObject {
    protected final String name;
    protected final ITransform transform;
    protected final ICollider collider;
    protected IShape shape;
    protected final IBehaviour behaviour;
    private GameEngine engine; // Referência ao motor de jogo

    /**
     * Constrói um novo objeto de jogo com os componentes especificados.
     * @param name Nome do objeto. Não deve ser nulo.
     * @param transform Transformação inicial do objeto (posição, rotação, etc.). Não deve ser nulo.
     * @param collider Colisor associado ao objeto. Não deve ser nulo.
     * @param shape Forma visual para renderização. Não deve ser nula.
     * @param behaviour Comportamento que define a lógica do objeto. Não deve ser nulo.
     * @post O objeto é criado com os componentes fornecidos.
     * @post O 'behaviour' fornecido é associado a este GameObject (this.behaviour.gameObject(this)).
     */
    public GameObject(String name, ITransform transform, ICollider collider, IShape shape, IBehaviour behaviour) {
        this.name = name;
        this.transform = transform;
        this.collider = collider;
        this.shape = shape;
        this.behaviour = behaviour;
        if (this.behaviour != null) { // Salvaguarda
            this.behaviour.gameObject(this);
        }
    }

    /**
     * Move o objeto aplicando deslocamento à sua transformação e mudança de camada.
     * (Nota: A movimentação do colisor é geralmente tratada pelo seu método onUpdate(),
     * sincronizando com a transformação do GameObject.)
     * @param dPos Vetor de deslocamento (dx, dy) a ser aplicado. Não deve ser nulo.
     * @param dLayer Variação da camada.
     * @post A posição e camada da 'transform' do objeto são atualizadas.
     */
    public void move(Point dPos, int dLayer) {
        transform.move(dPos, dLayer);
        // O colisor deve ser atualizado através do seu método onUpdate()
        // que lê da transformação do GameObject. Chamadas diretas a collider.move() aqui
        // podem ser redundantes ou levar a dessincronização se não forem geridas cuidadosamente.
    }

    /**
     * Rotaciona o objeto aplicando uma variação de ângulo à sua transformação.
     * (Nota: A rotação do colisor é geralmente tratada pelo seu método onUpdate().)
     * @param dTheta Ângulo de rotação em graus (positivo para sentido horário, dependendo da convenção).
     * @post O ângulo da 'transform' do objeto é atualizado.
     * @post O 'collider' é instruído a rotacionar (embora a sua atualização principal deva vir de onUpdate).
     */
    public void rotate(double dTheta) {
        transform.rotate(dTheta);
        if (collider != null) { // Salvaguarda
            collider.rotate(dTheta); // Esta chamada pode ser redundante se onUpdate() do colisor for robusto.
        }
    }

    /**
     * Aplica uma variação de escala ao objeto através da sua transformação.
     * (Nota: A escala do colisor é geralmente tratada pelo seu método onUpdate().)
     * @param dScale Variação de escala relativa (ex: 0.1 aumenta em 10%).
     * @post O fator de escala da 'transform' do objeto é atualizado.
     * @post O 'collider' é instruído a escalar (embora a sua atualização principal deva vir de onUpdate).
     */
    public void scale(double dScale) {
        transform.scale(dScale);
        if (collider != null) { // Salvaguarda
            collider.scale(dScale); // Esta chamada pode ser redundante.
        }
    }

    /**
     * Devolve o nome do objeto.
     * @return O nome do objeto.
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * Devolve a transformação do objeto (posição, rotação, escala e camada).
     * @return A instância de ITransform associada.
     */
    @Override
    public ITransform transform() {
        return transform;
    }

    /**
     * Devolve o colisor associado ao objeto.
     * @return A instância de ICollider associada.
     */
    @Override
    public ICollider collider() {
        return collider;
    }

    /**
     * Devolve a forma visual do objeto.
     * @return A instância de IShape associada.
     */
    @Override
    public IShape shape() {
        return shape;
    }

    /**
     * Altera a forma visual (IShape) deste objeto de jogo.
     * @param newShape A nova IShape a ser usada. Não deve ser nula.
     * @post A referência 'shape' interna é atualizada para 'newShape'.
     */
    @Override
    public void changeShape(IShape newShape) {
        this.shape = newShape;
    }

    /**
     * Devolve o comportamento do objeto.
     * @return A instância de IBehaviour associada.
     */
    @Override
    public IBehaviour behaviour() {
        return behaviour;
    }

    /**
     * Devolve uma representação textual do objeto, incluindo nome, transformação e colisor.
     * @return String formatada com os dados do objeto.
     */
    @Override
    public String toString() {
        return String.format(Locale.US, "%s\n(%.2f,%.2f) %d %.2f %.2f\n%s",
                name,
                transform.position().getX(), transform.position().getY(),
                transform.layer(), transform.angle(), transform.scale(),
                collider.toString());
    }

    /**
     * Devolve a instância do GameEngine à qual este objeto está associado.
     * @return A instância de GameEngine, ou nulo se não estiver associado.
     */
    @Override
    public GameEngine engine() {
        return engine;
    }

    /**
     * Define a instância do GameEngine para este objeto.
     * @param engine A instância de GameEngine a ser associada.
     * @post A referência interna 'engine' é definida como 'engine'.
     */
    @Override
    public void setEngine(GameEngine engine) {
        this.engine = engine;
    }
}