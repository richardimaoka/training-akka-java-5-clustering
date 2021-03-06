package org.mvrck.training.app;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import akka.cluster.sharding.typed.javadsl.*;
import com.typesafe.config.*;
import org.mvrck.training.actor.*;

public class BackendMain {
  public static void main(String[] args) throws Exception {
    /********************************************************************************
     *  Initialize System, Cluster, and ShardRegion
     *******************************************************************************/
    var config = ConfigFactory.load("backend-main.conf");
    var system = ActorSystem.create(Behaviors.<Void>empty(), "MaverickTraining", config);
    var sharding = ClusterSharding.get(system);

    // ShardRegions start
    sharding.init(Entity.of(OrderActor.ENTITY_TYPE_KEY, ctx -> OrderActor.create(ctx.getEntityId())).withRole("backend"));
    sharding.init(Entity.of(TicketStockActor.ENTITY_TYPE_KEY, ctx -> TicketStockActor.create(sharding, ctx.getEntityId())).withRole("backend"));

    /********************************************************************************
     *  Initialize TicketStock actors
     *******************************************************************************/
    var ref1 = sharding.entityRefFor(TicketStockActor.ENTITY_TYPE_KEY,"1");
    ref1.tell(new TicketStockActor.CreateTicketStock(1, 5000));
    var ref2 = sharding.entityRefFor(TicketStockActor.ENTITY_TYPE_KEY,"2");
    ref2.tell(new TicketStockActor.CreateTicketStock(2, 2000));
  }
}