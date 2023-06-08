package services;

import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Base {
    protected static Client client = null;

    protected static TdApi.AuthorizationState authorizationState = null;
    protected static volatile boolean haveAuthorization = false;
    protected static volatile boolean needQuit = false;
    protected static volatile boolean canQuit = false;

    protected static final Client.ResultHandler defaultHandler = new services.Handler.DefaultHandler();

    protected static final Lock authorizationLock = new ReentrantLock();
    protected static final Condition gotAuthorization = authorizationLock.newCondition();

    protected static final ConcurrentMap<Long, TdApi.User> users = new ConcurrentHashMap<Long, TdApi.User>();
    protected static final ConcurrentMap<Long, TdApi.BasicGroup> basicGroups = new ConcurrentHashMap<Long, TdApi.BasicGroup>();
    protected static final ConcurrentMap<Long, TdApi.Supergroup> supergroups = new ConcurrentHashMap<Long, TdApi.Supergroup>();
    protected static final ConcurrentMap<Integer, TdApi.SecretChat> secretChats = new ConcurrentHashMap<Integer, TdApi.SecretChat>();

    protected static final ConcurrentMap<Long, TdApi.Chat> chats = new ConcurrentHashMap<Long, TdApi.Chat>();
    protected static final NavigableSet<ChatOrder.OrderedChat> mainChatList = new TreeSet<ChatOrder.OrderedChat>();
    protected static boolean haveFullMainChatList = false;

    protected static final ConcurrentMap<Long, TdApi.UserFullInfo> usersFullInfo = new ConcurrentHashMap<Long, TdApi.UserFullInfo>();
    protected static final ConcurrentMap<Long, TdApi.BasicGroupFullInfo> basicGroupsFullInfo = new ConcurrentHashMap<Long, TdApi.BasicGroupFullInfo>();
    protected static final ConcurrentMap<Long, TdApi.SupergroupFullInfo> supergroupsFullInfo = new ConcurrentHashMap<Long, TdApi.SupergroupFullInfo>();

    protected static final String newLine = System.getProperty("line.separator");
    protected static final String commandsLine = "Enter command (help - List of commands): ";
    protected static volatile String currentPrompt = null;
    protected static String jsonString;
}