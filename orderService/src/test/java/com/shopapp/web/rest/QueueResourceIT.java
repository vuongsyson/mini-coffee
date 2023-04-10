package com.shopapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.shopapp.IntegrationTest;
import com.shopapp.domain.Queue;
import com.shopapp.repository.QueueRepository;
import com.shopapp.service.dto.QueueDTO;
import com.shopapp.service.mapper.QueueMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link QueueResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class QueueResourceIT {

    private static final Long DEFAULT_SHOP_ID = 1L;
    private static final Long UPDATED_SHOP_ID = 2L;

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final String ENTITY_API_URL = "/api/queues";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private QueueRepository queueRepository;

    @Autowired
    private QueueMapper queueMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restQueueMockMvc;

    private Queue queue;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Queue createEntity(EntityManager em) {
        Queue queue = new Queue().shopId(DEFAULT_SHOP_ID).code(DEFAULT_CODE).active(DEFAULT_ACTIVE);
        return queue;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Queue createUpdatedEntity(EntityManager em) {
        Queue queue = new Queue().shopId(UPDATED_SHOP_ID).code(UPDATED_CODE).active(UPDATED_ACTIVE);
        return queue;
    }

    @BeforeEach
    public void initTest() {
        queue = createEntity(em);
    }

    @Test
    @Transactional
    void createQueue() throws Exception {
        int databaseSizeBeforeCreate = queueRepository.findAll().size();
        // Create the Queue
        QueueDTO queueDTO = queueMapper.toDto(queue);
        restQueueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(queueDTO)))
            .andExpect(status().isCreated());

        // Validate the Queue in the database
        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeCreate + 1);
        Queue testQueue = queueList.get(queueList.size() - 1);
        assertThat(testQueue.getShopId()).isEqualTo(DEFAULT_SHOP_ID);
        assertThat(testQueue.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testQueue.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void createQueueWithExistingId() throws Exception {
        // Create the Queue with an existing ID
        queue.setId(1L);
        QueueDTO queueDTO = queueMapper.toDto(queue);

        int databaseSizeBeforeCreate = queueRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restQueueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(queueDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Queue in the database
        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkShopIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = queueRepository.findAll().size();
        // set the field null
        queue.setShopId(null);

        // Create the Queue, which fails.
        QueueDTO queueDTO = queueMapper.toDto(queue);

        restQueueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(queueDTO)))
            .andExpect(status().isBadRequest());

        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkActiveIsRequired() throws Exception {
        int databaseSizeBeforeTest = queueRepository.findAll().size();
        // set the field null
        queue.setActive(null);

        // Create the Queue, which fails.
        QueueDTO queueDTO = queueMapper.toDto(queue);

        restQueueMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(queueDTO)))
            .andExpect(status().isBadRequest());

        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllQueues() throws Exception {
        // Initialize the database
        queueRepository.saveAndFlush(queue);

        // Get all the queueList
        restQueueMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(queue.getId().intValue())))
            .andExpect(jsonPath("$.[*].shopId").value(hasItem(DEFAULT_SHOP_ID.intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())));
    }

    @Test
    @Transactional
    void getQueue() throws Exception {
        // Initialize the database
        queueRepository.saveAndFlush(queue);

        // Get the queue
        restQueueMockMvc
            .perform(get(ENTITY_API_URL_ID, queue.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(queue.getId().intValue()))
            .andExpect(jsonPath("$.shopId").value(DEFAULT_SHOP_ID.intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingQueue() throws Exception {
        // Get the queue
        restQueueMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingQueue() throws Exception {
        // Initialize the database
        queueRepository.saveAndFlush(queue);

        int databaseSizeBeforeUpdate = queueRepository.findAll().size();

        // Update the queue
        Queue updatedQueue = queueRepository.findById(queue.getId()).get();
        // Disconnect from session so that the updates on updatedQueue are not directly saved in db
        em.detach(updatedQueue);
        updatedQueue.shopId(UPDATED_SHOP_ID).code(UPDATED_CODE).active(UPDATED_ACTIVE);
        QueueDTO queueDTO = queueMapper.toDto(updatedQueue);

        restQueueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, queueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(queueDTO))
            )
            .andExpect(status().isOk());

        // Validate the Queue in the database
        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeUpdate);
        Queue testQueue = queueList.get(queueList.size() - 1);
        assertThat(testQueue.getShopId()).isEqualTo(UPDATED_SHOP_ID);
        assertThat(testQueue.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testQueue.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void putNonExistingQueue() throws Exception {
        int databaseSizeBeforeUpdate = queueRepository.findAll().size();
        queue.setId(count.incrementAndGet());

        // Create the Queue
        QueueDTO queueDTO = queueMapper.toDto(queue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQueueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, queueDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(queueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Queue in the database
        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchQueue() throws Exception {
        int databaseSizeBeforeUpdate = queueRepository.findAll().size();
        queue.setId(count.incrementAndGet());

        // Create the Queue
        QueueDTO queueDTO = queueMapper.toDto(queue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQueueMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(queueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Queue in the database
        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamQueue() throws Exception {
        int databaseSizeBeforeUpdate = queueRepository.findAll().size();
        queue.setId(count.incrementAndGet());

        // Create the Queue
        QueueDTO queueDTO = queueMapper.toDto(queue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQueueMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(queueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Queue in the database
        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateQueueWithPatch() throws Exception {
        // Initialize the database
        queueRepository.saveAndFlush(queue);

        int databaseSizeBeforeUpdate = queueRepository.findAll().size();

        // Update the queue using partial update
        Queue partialUpdatedQueue = new Queue();
        partialUpdatedQueue.setId(queue.getId());

        partialUpdatedQueue.code(UPDATED_CODE);

        restQueueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQueue.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQueue))
            )
            .andExpect(status().isOk());

        // Validate the Queue in the database
        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeUpdate);
        Queue testQueue = queueList.get(queueList.size() - 1);
        assertThat(testQueue.getShopId()).isEqualTo(DEFAULT_SHOP_ID);
        assertThat(testQueue.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testQueue.getActive()).isEqualTo(DEFAULT_ACTIVE);
    }

    @Test
    @Transactional
    void fullUpdateQueueWithPatch() throws Exception {
        // Initialize the database
        queueRepository.saveAndFlush(queue);

        int databaseSizeBeforeUpdate = queueRepository.findAll().size();

        // Update the queue using partial update
        Queue partialUpdatedQueue = new Queue();
        partialUpdatedQueue.setId(queue.getId());

        partialUpdatedQueue.shopId(UPDATED_SHOP_ID).code(UPDATED_CODE).active(UPDATED_ACTIVE);

        restQueueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedQueue.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedQueue))
            )
            .andExpect(status().isOk());

        // Validate the Queue in the database
        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeUpdate);
        Queue testQueue = queueList.get(queueList.size() - 1);
        assertThat(testQueue.getShopId()).isEqualTo(UPDATED_SHOP_ID);
        assertThat(testQueue.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testQueue.getActive()).isEqualTo(UPDATED_ACTIVE);
    }

    @Test
    @Transactional
    void patchNonExistingQueue() throws Exception {
        int databaseSizeBeforeUpdate = queueRepository.findAll().size();
        queue.setId(count.incrementAndGet());

        // Create the Queue
        QueueDTO queueDTO = queueMapper.toDto(queue);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restQueueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, queueDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(queueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Queue in the database
        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchQueue() throws Exception {
        int databaseSizeBeforeUpdate = queueRepository.findAll().size();
        queue.setId(count.incrementAndGet());

        // Create the Queue
        QueueDTO queueDTO = queueMapper.toDto(queue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQueueMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(queueDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Queue in the database
        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamQueue() throws Exception {
        int databaseSizeBeforeUpdate = queueRepository.findAll().size();
        queue.setId(count.incrementAndGet());

        // Create the Queue
        QueueDTO queueDTO = queueMapper.toDto(queue);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restQueueMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(queueDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Queue in the database
        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteQueue() throws Exception {
        // Initialize the database
        queueRepository.saveAndFlush(queue);

        int databaseSizeBeforeDelete = queueRepository.findAll().size();

        // Delete the queue
        restQueueMockMvc
            .perform(delete(ENTITY_API_URL_ID, queue.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Queue> queueList = queueRepository.findAll();
        assertThat(queueList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
