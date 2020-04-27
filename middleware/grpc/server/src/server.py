from concurrent import futures
import time
from google.protobuf.json_format import MessageToDict, ParseDict
import json
import grpc
import events_pb2
import events_pb2_grpc
import pprint



class Server(events_pb2_grpc.Server):

    filename = "events.json"

    def __init__(self):
        try:
            with open(Server.filename, "rb") as f:
                self.events = json.load(f)
        except FileNotFoundError:
            print("File not accessible")
            self.events = {}

        pprint.pprint(self.events)

    def SubscribeTopic(self, request, context):
        print(f"subscribe is called on topic: {request.topic}")

        processed = 0

        if request.timestamp != 0:
            events = self.events.get(request.topic, [()])

            for (ts, _) in events:
                if ts < request.timestamp:
                    processed += 1
                else:
                    break

        while context.is_active():
            if request.topic in self.events :
                events = self.events.get(request.topic)

                for (ts, e) in events[processed:]:
                    processed += 1
                    yield events_pb2.EventWrapper(timestamp=int(ts), event=ParseDict(e, events_pb2.Event()))
            time.sleep(1)

    def AddEvent(self, request, context):

        timestamp = int(time.time())

        req_dict = MessageToDict(request)

        for tag in request.topic:
            events_list = self.events.get(tag, [])
            events_list.append((timestamp, req_dict))
            self.events[tag] = events_list
            print(f"{request.topic} : {len(self.events[tag])} ts: {timestamp}")

        with open(Server.filename, "w+") as f:
            json.dump(self.events, f, indent=4)

        print("im here")
        return events_pb2.Response(code=200)

    def GetTopics(self, request, context):
        return events_pb2.TopicList(topic=self.events.keys())


def serve():
    server = grpc.server(futures.ThreadPoolExecutor(max_workers=10))
    events_pb2_grpc.add_ServerServicer_to_server(Server(), server)
    server.add_insecure_port("[::]:50051")
    server.start()
    server.wait_for_termination()


if __name__ == "__main__":
    serve()
