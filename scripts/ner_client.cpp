/*
Original author: https://github.com/ikillbombs
 */


#include <stdio.h>
#include <unistd.h>
#include <sys/time.h>

#include <thrift/protocol/TBinaryProtocol.h>
#include <thrift/transport/TSocket.h>
#include <thrift/transport/TTransportUtils.h>

using namespace std;
using namespace apache::thrift;
using namespace apache::thrift::protocol;
using namespace apache::thrift::transport;
using namespace boost;


void thriftConnect::setup() {
  shared_ptr<TTransport> socket(new TSocket("slave03.research.ets.org", 9999));
  shared_ptr<TTransport> transport(new TBufferedTransport(socket));
  shared_ptr<TProtocol> protocol(new TBinaryProtocol(transport));
  StanfordCoreNLPClient  client(protocol);


  try {
    transport->open();

    //client.ping();
    client.send_ping();
    printf("ping()\n");
    std::vector<NamedEntity> _NamedEntityReturn;
    client.get_entities_from_text(_NamedEntityReturn, "All of the surviving members of comedy group Monty Python are to reform for a stage show, one of the Pythons, Terry Jones, has confirmed.");
    transport->close();
  } catch (TException &tx) {
    printf("ERROR: %s\n", tx.what());
  }
}
