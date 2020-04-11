# SPACE AGENCY

Purpose of this project is to become familiar with standard RMI API.

## Requirements
This task is to implement a system that mediates communication between Space Agencies and Space Transport Carriers. SA might order one of three types of services - carriage of the shipload, transport of people or to put satelitte on orbit. The system must to be designed using RabbitIMQ.

All actions are included in secrect space company cooperation, on the following conditions:
- All services prices are equal
- Carrier might only provide two services, he must declare those before joining the company
- Orders must be loadbalanced
- Orders must be processed exclusive
- Orders are identified by Agency name and unique identifier
- Carrier has to acknowledge Agency after finalizing order 

Premium version provides administration module, that enables to monitor system traffic and send messages to all participants of SpaceAgencies and Carriers groups. 

## Setup RabbitIMQ

```$xslt
 docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

## Architecture

![diagram](img/diagram.png)
