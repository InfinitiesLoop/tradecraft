#!/usr/bin/env node
'use strict';

const program = require('commander');
const net = require('net');
const pkg = require('./package.json');
const readline = require('readline');

program
  .version(pkg.version)
  .option('-h, --host [host:port]', 'The TradeCraft server to connect to.', 'localhost:8088')
  .parse(process.argv);

const [ _, server, port ] = /(.*):(.*)/i.exec(program.host);

connect(server, port);

function connect(server, port) {
  const client = new net.Socket();
  const context = { client };

  context.rl = readline.createInterface({
    input: process.stdin,
    output: null
  });

  context.rl.on('line', answer => {
    if (context.prompt) {
      const { defaultValue, route } = context.prompt;
      context.prompt = null;
      send(context, { type: "answer", "route": route, param: answer });
    }
  });

  client.connect(port, server, function() {
  });

  client.on('data', data => {
    const command = JSON.parse(data.toString('utf-8'));
    handleCommand(context, command);
  });

  client.on('close', () => {
    console.log('Connection closed.');
  });
}

function handleCommand(context, command) {
  switch(command.type) {
    case 'render':
      write(command.render);
      break;
    case 'prompt':
      write(command.render);
      prompt(context, command.default, command.route);
      break;
    case 'redirect':
      send(context, { type: 'command', route: command.route });
      break;
    default:
      console.error('CLIENT ERROR: Unknown server command: ' + JSON.stringify(command));
  }
}

function prompt(context, defaultValue, route) {
  context.prompt = { defaultValue: defaultValue, route: route };
  context.rl.prompt();
}

function send(context, message) {
  context.client.write(JSON.stringify(message));
}

function write(text) {
  process.stdout.write(text);
}