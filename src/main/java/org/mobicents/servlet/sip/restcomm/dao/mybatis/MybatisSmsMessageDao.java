package org.mobicents.servlet.sip.restcomm.dao.mybatis;

import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.joda.time.DateTime;
import org.mobicents.servlet.sip.restcomm.Sid;
import org.mobicents.servlet.sip.restcomm.SmsMessage;
import org.mobicents.servlet.sip.restcomm.annotations.concurrency.ThreadSafe;
import org.mobicents.servlet.sip.restcomm.dao.SmsMessageDao;

@ThreadSafe public final class MybatisSmsMessageDao implements SmsMessageDao {
  private static final String namespace = "org.mobicents.servlet.sip.restcomm.dao.SmsMessageDao.";
  private final SqlSessionFactory sessions;
  
  public MybatisSmsMessageDao(final SqlSessionFactory sessions) {
    super();
    this.sessions = sessions;
  }
  
  @Override public void addSmsMessage(final SmsMessage smsMessage) {
    final SqlSession session = sessions.openSession();
    try {
      session.insert(namespace + "addSmsMessage", toMap(smsMessage));
    } finally {
      session.close();
    }
  }

  @Override public SmsMessage getSmsMessage(final Sid sid) {
    final SqlSession session = sessions.openSession();
    try {
      @SuppressWarnings("unchecked")
      final Map<String, Object> result = (Map<String, Object>)session.selectOne(namespace + "getSmsMessage", sid.toString());
      if(result != null) {
        return toSmsMessage(result);
      } else {
        return null;
      }
    } finally {
      session.close();
    }
  }

  @Override public List<SmsMessage> getSmsMessages(final Sid accountSid) {
    final SqlSession session = sessions.openSession();
    try {
      @SuppressWarnings("unchecked")
      final List<Map<String, Object>> results = (List<Map<String, Object>>)session.selectList(namespace + "getSmsMessages", accountSid.toString());
      final List<SmsMessage> smsMessages = new ArrayList<SmsMessage>();
      if(results != null && !results.isEmpty()) {
        for(final Map<String, Object> result : results) {
          smsMessages.add(toSmsMessage(result));
        }
      }
      return smsMessages;
    } finally {
      session.close();
    }
  }

  @Override public void removeSmsMessage(final Sid sid) {
    deleteSmsMessage(namespace + "removeSmsMessage", sid);
  }

  @Override public void removeSmsMessages(final Sid accountSid) {
    deleteSmsMessage(namespace + "removeSmsMessages", accountSid);
  }
  
  private void deleteSmsMessage(final String selector, final Sid sid) {
    final SqlSession session = sessions.openSession();
    try {
      session.delete(selector, sid.toString());
    } finally {
      session.close();
    }
  }
  
  private Map<String, Object> toMap(final SmsMessage smsMessage) {
    final Map<String, Object> map = new HashMap<String, Object>();
    map.put("sid", smsMessage.getSid().toString());
    map.put("date_created", smsMessage.getDateCreated().toDate());
    map.put("date_updated", smsMessage.getDateUpdated().toDate());
    map.put("date_sent", smsMessage.getDateSent().toDate());
    map.put("account_sid", smsMessage.getAccountSid().toString());
    map.put("sender", smsMessage.getSender());
    map.put("recipient", smsMessage.getRecipient());
    map.put("body", smsMessage.getBody());
    map.put("status", smsMessage.getStatus());
    map.put("direction", smsMessage.getDirection());
    map.put("price", smsMessage.getPrice().toString());
    map.put("api_version", smsMessage.getApiVersion());
    map.put("uri", smsMessage.getUri().toString());
    return map;
  }
  
  private SmsMessage toSmsMessage(final Map<String, Object> map) {
    final Sid sid = new Sid((String)map.get("sid"));
    final DateTime dateCreated = new DateTime((Date)map.get("date_created"));
    final DateTime dateUpdated = new DateTime((Date)map.get("date_updated"));
    final DateTime dateSent = new DateTime((Date)map.get("date_sent"));
    final Sid accountSid = new Sid((String)map.get("account_sid"));
    final String sender = (String)map.get("sender");
    final String recipient = (String)map.get("recipient");
    final String body = (String)map.get("body");
    final String status = (String)map.get("status");
    final String direction = (String)map.get("direction");
    final BigDecimal price = new BigDecimal((String)map.get("price"));
    final String apiVersion = (String)map.get("api_version");
    final URI uri = URI.create((String)map.get("uri"));
    return new SmsMessage(sid, dateCreated, dateUpdated, dateSent, accountSid, sender, recipient, body, status,
        direction, price, apiVersion, uri);
  }
}